package io.github.jokoframework.myproject.basic.dto;

import io.github.jokoframework.common.dto.JokoBaseResponse;
import java.util.List;

/**
 * Response DTO for notifications with metadata
 *
 * @author FedeTraversi
 */
public class NotificationResponseDTO extends JokoBaseResponse {
    
    private List<NotificationDTO> data;
    private MetadataInfo metadata;

    public static class MetadataInfo {
        private int total;
        private String timestamp;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }

    public List<NotificationDTO> getData() {
        return data;
    }

    public void setData(List<NotificationDTO> data) {
        this.data = data;
    }

    public MetadataInfo getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataInfo metadata) {
        this.metadata = metadata;
    }
}