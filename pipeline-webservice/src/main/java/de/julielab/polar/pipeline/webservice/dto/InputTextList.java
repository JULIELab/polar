package de.julielab.polar.pipeline.webservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class InputTextList {
    private List<InputText> texts;
}
