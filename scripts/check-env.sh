#!/usr/bin/env bash
#
# check-env.sh — Pre-vuelo de red para el joko_backend_starter_kit.
#
# Comprueba que esta máquina puede hablar HTTPS con los sitios de descarga
# que usa turn-key.sh (SDKMAN, GitHub, Maven Central, Adoptium) y, si el
# handshake TLS falla, lista la URL y el certificado que presentó el peer.
#
# En laboratorios con proxy transparente en el puerto 443 el síntoma típico
# es un certificado de la CA de la universidad (inspección SSL / Squid) que
# curl, Git o el cacerts de Java no confían. La hoja (p. ej. CN=*.docker.com)
# no se importa: hace falta la CA de la cadena (p. ej. Lab Squid Intercept CA).
#
# Uso:
#   ./scripts/check-env.sh
#   ./scripts/check-env.sh -v
#   ./scripts/check-env.sh --save-ca ./intercept-ca.pem
#   ./scripts/check-env.sh --no-save-ca
#   CONNECT_TIMEOUT=5 ./scripts/check-env.sh
#
# Variables de entorno (opcionales):
#   CONNECT_TIMEOUT   segundos para TCP+TLS (default: 10)
#   CHECK_ENV_URLS    URLs extra, separadas por espacio
#
# Códigos de salida:
#   0  todos los sitios respondieron con TLS válido
#   1  al menos un sitio falló
#   2  faltan herramientas (curl u openssl)

set -uo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CONNECT_TIMEOUT="${CONNECT_TIMEOUT:-10}"
VERBOSE=0
SAVE_CA="auto"
FAIL_COUNT=0
TLS_FAIL_COUNT=0
PUBLIC_CA_WARN=0
INTERCEPT_CA_PEM=""
INTERCEPT_CA_FP=""
INTERCEPT_CA_HITS=0

say()    { printf '\n\033[1;36m==> %s\033[0m\n' "$*"; }
ok()     { printf '  \033[1;32m[OK]\033[0m   %s\n' "$*"; }
warn()   { printf '  \033[1;33m[!]\033[0m    %s\n' "$*"; }
fail()   { printf '  \033[1;31m[FAIL]\033[0m %s\n' "$*"; }
indent() { printf '         %s\n' "$*"; }

usage() {
    sed -n '2,32p' "$0" | sed 's/^# \?//'
}

while [ $# -gt 0 ]; do
    case "$1" in
        -h|--help) usage; exit 0 ;;
        -v|--verbose) VERBOSE=1; shift ;;
        --no-save-ca) SAVE_CA="none"; shift ;;
        --save-ca)
            if [ $# -lt 2 ]; then
                printf 'ERROR: --save-ca requiere un archivo.\n' >&2
                exit 2
            fi
            SAVE_CA="$2"
            shift 2
            ;;
        *)
            printf 'ERROR: argumento desconocido: %s\n' "$1" >&2
            usage >&2
            exit 2
            ;;
    esac
done

need() {
    command -v "$1" >/dev/null 2>&1 || {
        printf 'ERROR: se necesita "%s" en el PATH.\n' "$1" >&2
        exit 2
    }
}

need curl
need openssl

with_timeout() {
    local secs="$1"
    shift
    if command -v timeout >/dev/null 2>&1; then
        timeout "$secs" "$@"
    elif command -v gtimeout >/dev/null 2>&1; then
        gtimeout "$secs" "$@"
    else
        "$@"
    fi
}

host_of() {
    local rest="${1#*://}"
    rest="${rest%%/*}"
    rest="${rest%%\?*}"
    printf '%s\n' "${rest%%:*}"
}

port_of() {
    local url="$1"
    local rest="${url#*://}"
    rest="${rest%%/*}"
    rest="${rest%%\?*}"
    case "$rest" in
        *:*) printf '%s\n' "${rest##*:}" ;;
        *)
            case "$url" in
                http://*) printf '80\n' ;;
                *)        printf '443\n' ;;
            esac
            ;;
    esac
}

peer_sclient() {
    local host="$1" port="$2"
    printf '\n' | with_timeout "$CONNECT_TIMEOUT" \
        openssl s_client -connect "${host}:${port}" -servername "$host" \
            -showcerts 2>&1 || true
}

pem_line() {
    local pem="$1"
    shift
    printf '%s\n' "$pem" | openssl x509 -noout "$@" 2>/dev/null || true
}

normalize_dn() {
    printf '%s\n' "$1" | sed 's/^subject=//;s/^issuer=//;s/ = /=/g'
}

pem_fp() {
    pem_line "$1" -fingerprint -sha256 | sed 's/^.*Fingerprint=//'
}

# Extrae certificados de un dump de s_client. Rellena CHAIN_PEMS (array) y CHAIN_N.
parse_chain() {
    local raw="$1"
    local pem="" in=0 line
    CHAIN_PEMS=()
    CHAIN_N=0
    while IFS= read -r line || [ -n "$line" ]; do
        case "$line" in
            -----BEGIN\ CERTIFICATE-----)
                in=1
                pem="$line"$'\n'
                ;;
            -----END\ CERTIFICATE-----)
                pem+="$line"$'\n'
                CHAIN_PEMS+=("$pem")
                CHAIN_N=$((CHAIN_N + 1))
                in=0
                pem=""
                ;;
            *)
                if [ "$in" -eq 1 ]; then
                    pem+="$line"$'\n'
                fi
                ;;
        esac
    done <<< "$raw"
}

# Dada la cadena, elige la hoja y la CA interceptora (si hay).
# Rellena LEAF_PEM y CA_PEM.
pick_leaf_and_ca() {
    local i leaf_iss leaf_sub c_sub c_iss
    LEAF_PEM=""
    CA_PEM=""
    [ "$CHAIN_N" -ge 1 ] || return 0
    LEAF_PEM="${CHAIN_PEMS[0]}"
    leaf_iss="$(normalize_dn "$(pem_line "$LEAF_PEM" -issuer)")"
    leaf_sub="$(normalize_dn "$(pem_line "$LEAF_PEM" -subject)")"

    for i in "${!CHAIN_PEMS[@]}"; do
        [ "$i" -eq 0 ] && continue
        c_sub="$(normalize_dn "$(pem_line "${CHAIN_PEMS[$i]}" -subject)")"
        c_iss="$(normalize_dn "$(pem_line "${CHAIN_PEMS[$i]}" -issuer)")"
        if [ -n "$c_sub" ] && [ "$c_sub" = "$leaf_iss" ]; then
            CA_PEM="${CHAIN_PEMS[$i]}"
            break
        fi
        if [ -n "$c_sub" ] && [ "$c_sub" = "$c_iss" ]; then
            CA_PEM="${CHAIN_PEMS[$i]}"
            break
        fi
    done

    if [ -z "$CA_PEM" ] && [ -n "$leaf_sub" ] && [ "$leaf_sub" = "$leaf_iss" ]; then
        CA_PEM="$LEAF_PEM"
    fi
}

summarize_pem() {
    local pem="$1"
    local subject issuer dates serial fp san
    subject="$(pem_line "$pem" -subject)"
    issuer="$(pem_line "$pem" -issuer)"
    dates="$(printf '%s\n' "$pem" | openssl x509 -noout -dates 2>/dev/null || true)"
    serial="$(pem_line "$pem" -serial)"
    fp="$(printf '%s\n' "$pem" | openssl x509 -noout -fingerprint -sha256 2>/dev/null || true)"
    san="$(printf '%s\n' "$pem" | openssl x509 -noout -ext subjectAltName 2>/dev/null \
        | grep -v '^X509v3' | grep -v '^$' || true)"
    if [ -z "$san" ]; then
        san="$(printf '%s\n' "$pem" | openssl x509 -noout -text 2>/dev/null \
            | awk '/Subject Alternative Name/ { getline; print; exit }' || true)"
    fi
    [ -n "$subject" ] && indent "$subject"
    [ -n "$issuer" ]  && indent "$issuer"
    if [ -n "$dates" ]; then
        printf '%s\n' "$dates" | while IFS= read -r line; do
            [ -n "$line" ] && indent "$line"
        done
    fi
    [ -n "$serial" ] && indent "$serial"
    [ -n "$fp" ]     && indent "$fp"
    if [ -n "$san" ]; then
        indent "X509v3 Subject Alternative Name:"
        printf '%s\n' "$san" | while IFS= read -r line; do
            line="${line#"${line%%[![:space:]]*}"}"
            [ -n "$line" ] && indent "  $line"
        done
    fi
}

issuer_looks_public() {
    echo "$1" | grep -qiE \
        'Let.s Encrypt|DigiCert|Amazon( RSA| Root| Trust)?|Google Trust Services|Sectigo|GlobalSign|Starfield|Go ?Daddy|Cloudflare|ISRG Root|IdenTrust|COMODO (CA|RSA)|Comodo CA|Entrust|Thawte|VeriSign|GeoTrust|Certum|GitHub|Microsoft (Azure|Corporation|RSA)|Apple (Inc|Certification)|USERTrust|AAA Certificate Services'
}

issuer_looks_intercept() {
    echo "$1" | grep -qiE \
        'Squid|Intercept|SSL.?Bump|pfSense|Forti(Gate|net)|Zscaler|BlueCoat|Blue Coat|Netskope|Cisco Umbrella|Sophos|Kerio|OPNsense'
}

remember_intercept_ca() {
    local pem="$1"
    local fp
    [ -n "$pem" ] || return 0
    fp="$(pem_fp "$pem")"
    if [ -z "$INTERCEPT_CA_PEM" ]; then
        INTERCEPT_CA_PEM="$pem"
        INTERCEPT_CA_FP="$fp"
        INTERCEPT_CA_HITS=1
        return 0
    fi
    if [ -n "$fp" ] && [ "$fp" = "$INTERCEPT_CA_FP" ]; then
        INTERCEPT_CA_HITS=$((INTERCEPT_CA_HITS + 1))
    fi
}

print_presented_cert() {
    local url="$1"
    local host port raw issuer
    host="$(host_of "$url")"
    port="$(port_of "$url")"
    raw="$(peer_sclient "$host" "$port")"
    parse_chain "$raw"
    pick_leaf_and_ca

    indent "URL:  $url"
    indent "Peer: ${host}:${port}"

    if [ -z "$LEAF_PEM" ]; then
        indent "El peer no presentó un certificado X.509 (o la conexión se cortó antes del handshake)."
        indent "Salida de openssl s_client (recorte):"
        printf '%s\n' "$raw" | head -n 25 | while IFS= read -r line; do
            indent "  $line"
        done
        return
    fi

    indent "Certificado presentado (hoja):"
    summarize_pem "$LEAF_PEM"

    issuer="$(pem_line "$LEAF_PEM" -issuer)"
    indent ""
    if [ -n "$issuer" ] && ! issuer_looks_public "$issuer"; then
        indent "El Issuer no parece una CA pública. En campus suele ser un proxy"
        indent "transparente con inspección SSL en el puerto 443."
        if issuer_looks_intercept "$issuer"; then
            indent "El nombre del emisor coincide con un proxy de inspección (p. ej. Squid SSL bump)."
        fi
        if [ -n "$CA_PEM" ]; then
            indent "CA de la cadena (esta es la que hay que importar, no la hoja):"
            summarize_pem "$CA_PEM"
            remember_intercept_ca "$CA_PEM"
            indent "PEM y pasos de importación: ver el resumen al final."
        else
            indent "La CA no vino en la cadena TLS; pedí el .pem a la red del campus."
        fi
    else
        indent "El emisor parece una CA pública; el fallo puede ser fecha, nombre o cadena incompleta."
    fi

    if [ "$VERBOSE" -eq 1 ]; then
        local i
        indent ""
        indent "Cadena completa ($CHAIN_N certificado(s)):"
        for i in "${!CHAIN_PEMS[@]}"; do
            indent "----- certificado $((i + 1)) -----"
            printf '%s\n' "${CHAIN_PEMS[$i]}" | while IFS= read -r line; do
                indent "  $line"
            done
        done
    fi
}

is_tls_curl_rc() {
    case "$1" in
        35|51|53|54|58|59|60|64|66|77|80) return 0 ;;
        *) return 1 ;;
    esac
}

curl_tls_label() {
    case "$1" in
        35) printf 'error de handshake SSL/TLS (curl 35)' ;;
        51) printf 'el certificado del peer no es válido (curl 51)' ;;
        58) printf 'problema con el certificado SSL local (curl 58)' ;;
        60) printf 'el certificado no está firmado por una CA conocida (curl 60)' ;;
        64) printf 'la conexión TLS no se pudo verificar (curl 64)' ;;
        77) printf 'problema leyendo el bundle de CAs (curl 77)' ;;
        *)  printf 'error TLS (curl %s)' "$1" ;;
    esac
}

# Imprime: RC=<n> HTTP=<code> ERR=<msg>
probe_url() {
    local url="$1"
    local blob rc code err
    blob="$(curl -sS -I -L --max-redirs 5 \
        --connect-timeout "$CONNECT_TIMEOUT" \
        --max-time "$CONNECT_TIMEOUT" \
        -o /dev/null -w '%{http_code}' \
        "$url" 2>&1)"
    rc=$?
    blob="$(printf '%s' "$blob" | tr -d '\r')"
    if [ "$rc" -eq 0 ]; then
        printf 'RC=0 HTTP=%s ERR=\n' "$blob"
        return
    fi
    code="$(printf '%s' "$blob" | grep -oE '[0-9]{3}$' || true)"
    err="$(printf '%s' "$blob" | sed -E 's/[0-9]{3}$//' | tr '\n' ' ')"
    err="$(printf '%s' "$err" | sed 's/ More details here:.*//')"
    err="$(printf '%s' "$err" | sed 's/[[:space:]]\{1,\}/ /g; s/^ //; s/ $//')"
    printf 'RC=%s HTTP=%s ERR=%s\n' "$rc" "${code:-}" "$err"
}

insecure_handshake_ok() {
    curl -sk -I -L --max-redirs 5 \
        --connect-timeout "$CONNECT_TIMEOUT" \
        --max-time "$CONNECT_TIMEOUT" \
        -o /dev/null -w '%{http_code}' \
        "$1" >/dev/null 2>&1
}

check_one() {
    local label="$1"
    local url="$2"
    local result rc http_code err

    result="$(probe_url "$url")"
    rc="$(printf '%s\n' "$result" | sed -n 's/^RC=\([0-9]*\).*/\1/p')"
    http_code="$(printf '%s\n' "$result" | sed -n 's/.*HTTP=\([0-9]*\).*/\1/p')"
    err="$(printf '%s\n' "$result" | sed -n 's/.*ERR=//p')"

    if [ "$rc" = "0" ]; then
        ok "$label"
        case "$http_code" in
            401|403|404)
                indent "$url  (HTTP ${http_code} — el host respondió; TLS OK)"
                ;;
            *)
                indent "$url  (HTTP ${http_code:-???})"
                ;;
        esac
        if [ "$VERBOSE" -eq 1 ]; then
            local host port raw
            host="$(host_of "$url")"
            port="$(port_of "$url")"
            raw="$(peer_sclient "$host" "$port")"
            parse_chain "$raw"
            pick_leaf_and_ca
            if [ -n "$LEAF_PEM" ]; then
                indent "$(pem_line "$LEAF_PEM" -subject)"
                indent "$(pem_line "$LEAF_PEM" -issuer)"
            fi
        fi
        return 0
    fi

    FAIL_COUNT=$((FAIL_COUNT + 1))
    fail "$label"
    indent "$url"
    if is_tls_curl_rc "$rc"; then
        indent "$(curl_tls_label "$rc")"
        [ -n "$err" ] && indent "$err"
        if insecure_handshake_ok "$url"; then
            indent "Con --insecure el handshake completa: el problema es el certificado (CA, nombre o fechas), no el protocolo TLS."
        else
            indent "Con --insecure el handshake también falla: el proxy/servidor corta TLS (protocolo, SNI o reset)."
        fi
        TLS_FAIL_COUNT=$((TLS_FAIL_COUNT + 1))
        print_presented_cert "$url"
    else
        case "$rc" in
            6)  indent "No se resolvió el nombre (DNS). curl $rc" ;;
            7)  indent "No se pudo conectar al host. curl $rc" ;;
            28) indent "Tiempo de espera agotado (${CONNECT_TIMEOUT}s). curl $rc" ;;
            *)  indent "curl salió con código $rc" ;;
        esac
        [ -n "$err" ] && indent "$err"
        if [ "$rc" != "6" ]; then
            print_presented_cert "$url"
        fi
    fi
    return 1
}

save_dest() {
    case "$SAVE_CA" in
        none) printf '\n' ;;
        auto) printf '%s\n' "${REPO_ROOT}/intercept-ca.pem" ;;
        *)    printf '%s\n' "$SAVE_CA" ;;
    esac
}

print_intercept_ca_report() {
    local dest
    [ -n "$INTERCEPT_CA_PEM" ] || return 0

    say "CA interceptora (la que hay que importar)"
    indent "Apareció en $INTERCEPT_CA_HITS sitio(s). No importes la hoja (p. ej. CN=*.docker.com)."
    summarize_pem "$INTERCEPT_CA_PEM"

    dest="$(save_dest)"
    if [ -n "$dest" ]; then
        printf '%s' "$INTERCEPT_CA_PEM" > "$dest"
        indent ""
        indent "PEM guardado en: $dest"
    else
        indent ""
        indent "PEM de la CA (no se guardó archivo: --no-save-ca):"
        printf '%s\n' "$INTERCEPT_CA_PEM" | while IFS= read -r line; do
            [ -n "$line" ] && indent "  $line"
        done
    fi

    indent ""
    indent "Sistema (curl, git, SDKMAN) — Debian/Ubuntu:"
    if [ -n "$dest" ]; then
        indent "  sudo cp \"$dest\" /usr/local/share/ca-certificates/campus-intercept.crt"
        indent "  sudo update-ca-certificates"
        indent "Java / Maven (cacerts propio, no el del sistema):"
        indent "  keytool -importcert -trustcacerts -alias campus-ca -file \"$dest\" \\"
        indent "    -keystore \"\$JAVA_HOME/lib/security/cacerts\""
    else
        indent "  Guardá el PEM de arriba en un .crt y luego:"
        indent "  sudo cp campus-intercept.crt /usr/local/share/ca-certificates/"
        indent "  sudo update-ca-certificates"
        indent "Java / Maven: keytool -importcert -trustcacerts -alias campus-ca -file <crt> \\"
        indent "    -keystore \"\$JAVA_HOME/lib/security/cacerts\""
    fi
    indent "Docker Engine no usa el cacerts de Java: tras update-ca-certificates,"
    indent "  sudo systemctl restart docker"
    indent "Después volvé a correr: ./scripts/check-env.sh"
}

print_tools() {
    say "Herramientas"
    indent "curl    $(curl --version 2>/dev/null | head -n1)"
    indent "openssl $(openssl version 2>/dev/null)"
    if command -v git >/dev/null 2>&1; then
        indent "git     $(git --version 2>/dev/null)"
    else
        warn "git no está en el PATH (turn-key lo necesita para clonar joko-utils)."
    fi
    if command -v java >/dev/null 2>&1; then
        indent "java    $(java -version 2>&1 | head -n1)"
        if [ -n "${JAVA_HOME:-}" ]; then
            indent "JAVA_HOME=$JAVA_HOME"
        fi
    else
        indent "java    (no está en el PATH; turn-key lo instala con SDKMAN si falta)"
    fi
    if command -v mvn >/dev/null 2>&1; then
        indent "mvn     $(mvn -v 2>/dev/null | head -n1)"
    else
        indent "mvn     (no está en el PATH; turn-key lo instala con SDKMAN si falta)"
    fi
}

print_proxy_env() {
    say "Proxy y almacenes de CAs"
    local found=0 var
    for var in http_proxy https_proxy HTTP_PROXY HTTPS_PROXY ALL_PROXY all_proxy \
               no_proxy NO_PROXY SSL_CERT_FILE SSL_CERT_DIR CURL_CA_BUNDLE \
               REQUESTS_CA_BUNDLE GIT_SSL_CAINFO; do
        if [ -n "${!var:-}" ]; then
            indent "$var=${!var}"
            found=1
        fi
    done
    if [ "$found" -eq 0 ]; then
        indent "No hay variables de proxy/CA en el entorno."
        indent "Un proxy transparente no necesita esas variables: intercepta el 443 igual."
    fi
}

print_tools
print_proxy_env

say "Sitios de descarga (TLS en 443)"

while IFS='|' read -r label url; do
    [ -z "${label:-}" ] && continue
    case "$label" in
        \#*) continue ;;
    esac
    check_one "$label" "$url" || true
done <<'EOF'
SDKMAN installer|https://get.sdkman.io
SDKMAN API|https://api.sdkman.io/2
GitHub|https://github.com
joko-utils (git)|https://github.com/jokoframework/joko-utils
Maven Central (ASF)|https://repo.maven.apache.org/maven2/
Maven Central (repo1)|https://repo1.maven.org/maven2/
Maven wrapper|https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
Adoptium (Temurin)|https://api.adoptium.net
OWASP NVD API|https://services.nvd.nist.gov
Docker Hub registry|https://registry-1.docker.io/v2/
EOF

if [ -n "${CHECK_ENV_URLS:-}" ]; then
    say "URLs extra (CHECK_ENV_URLS)"
    # shellcheck disable=SC2086
    for url in $CHECK_ENV_URLS; do
        check_one "$url" "$url" || true
    done
fi

if [ "$FAIL_COUNT" -eq 0 ]; then
    say "Emisores de los certificados (muestra)"
    for url in \
        https://repo.maven.apache.org/maven2/ \
        https://github.com \
        https://get.sdkman.io
    do
        host="$(host_of "$url")"
        port="$(port_of "$url")"
        raw="$(peer_sclient "$host" "$port")"
        parse_chain "$raw"
        pick_leaf_and_ca
        if [ -z "$LEAF_PEM" ]; then
            warn "$host: no se pudo leer el certificado (revisá con -v)"
            continue
        fi
        indent "$host"
        indent "  $(pem_line "$LEAF_PEM" -subject)"
        indent "  $(pem_line "$LEAF_PEM" -issuer)"
        issuer="$(pem_line "$LEAF_PEM" -issuer)"
        if [ -n "$issuer" ] && ! issuer_looks_public "$issuer"; then
            PUBLIC_CA_WARN=1
            if [ -n "$CA_PEM" ]; then
                remember_intercept_ca "$CA_PEM"
            fi
        fi
    done
    if [ "$PUBLIC_CA_WARN" -eq 1 ]; then
        warn "Uno o más emisores no parecen CA públicas: hay inspección SSL."
        indent "curl confía en esa CA (el sistema la tiene), pero Maven usa el cacerts de Java."
        indent "Si el build falla con 'PKIX path building failed', importá la CA de la red en Java."
    fi
fi

print_intercept_ca_report

say "Resumen"
if [ "$FAIL_COUNT" -eq 0 ] && [ "$PUBLIC_CA_WARN" -eq 0 ]; then
    ok "TLS correcto hacia los sitios de descarga. Podés seguir con ./scripts/turn-key.sh"
    exit 0
fi

if [ "$FAIL_COUNT" -eq 0 ] && [ "$PUBLIC_CA_WARN" -eq 1 ]; then
    warn "La conexión HTTPS funciona, pero el certificado lo emite una CA de inspección SSL."
    indent "Si Maven falla con PKIX, importá la CA de arriba en el cacerts de Java."
    exit 0
fi

fail "$FAIL_COUNT sitio(s) con problemas ($TLS_FAIL_COUNT de ellos por TLS/certificado)."
indent "Sin esos destinos, turn-key.sh no puede instalar SDKMAN, Java, Maven o las dependencias."
if [ -n "$INTERCEPT_CA_PEM" ]; then
    indent "Importá la CA interceptora del bloque anterior (sistema + cacerts Java) y re-ejecutá este script."
else
    indent "Si el Issuer es de la universidad: pedí el CA del proxy e importalo (sistema + cacerts Java)."
    indent "Re-ejecutá este script hasta que todos los sitios den [OK]."
fi
exit 1
