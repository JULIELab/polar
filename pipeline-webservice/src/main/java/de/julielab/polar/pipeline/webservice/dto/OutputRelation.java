package de.julielab.polar.pipeline.webservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
public class OutputRelation {
    private String doc_id;
    private String medication_text;
    private String disease_text;
    private String medication_category;
    private String disease_category;
    private int medication_offset;
    private int disease_offset;
    private int token_distance;
    private String coocurrence_type;
    private String context;
}
