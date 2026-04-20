package at.htl.weather.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Simple error response")
public class ErrorDto {

    @Schema(description = "Error message", example = "City with id 9999 was not found")
    public String message;

    public ErrorDto() {
    }

    public ErrorDto(String message) {
        this.message = message;
    }
}

