package com.lepikhina.model.data;

import com.lepikhina.model.persitstence.ConnectionPreset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserConfig {

    List<ConnectionPreset> connectionPresets;
}
