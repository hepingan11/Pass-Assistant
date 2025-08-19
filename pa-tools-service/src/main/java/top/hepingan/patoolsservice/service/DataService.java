package top.hepingan.patoolsservice.service;

import java.util.List;
import java.util.Map;

public interface DataService {

    List<Map<String,Integer>> timeFrequency(Integer type);

    List<Map<String, Object>> getWordFrequency(int v);

    List<Map<String,Integer>> getModelFrequency();
}
