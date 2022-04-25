package com.mikorpar.brbljavac_api.data.models;

import lombok.Data;

@Data
public class File {
    private String filename;
    private String filetype;
    private byte[] data;
}
