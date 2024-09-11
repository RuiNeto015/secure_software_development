package com.desofs.backend.domain.valueobjects;

import static org.apache.commons.lang3.Validate.*;

public class ImageUrl {

    private final Id id;
    private final String url;

    private ImageUrl(Id id, String url) {
        this.id = id;
        this.url = url;
    }

    public static ImageUrl create(Id id, String url) {
        notNull(url, "Id must not be null.");
        notNull(url, "URL must not be null.");
        notEmpty(url.trim(), "URL must not be empty.");

        return new ImageUrl(id, new String(url.trim()));
    }

    public ImageUrl copy() {
        return new ImageUrl(id, this.url);
    }

    public Id getId() {
        return this.id.copy();
    }

    public String getUrl() {
        return new String(this.url);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return this.url.equals(((ImageUrl) obj).url);
    }
}