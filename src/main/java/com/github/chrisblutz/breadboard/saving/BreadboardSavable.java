package com.github.chrisblutz.breadboard.saving;

import java.util.Map;

public interface BreadboardSavable {

    Map<String, Object> dumpToYAML(ProjectOutputWriter writer);

    void loadFromYAML(Map<String, Object> yamlMapping);
}
