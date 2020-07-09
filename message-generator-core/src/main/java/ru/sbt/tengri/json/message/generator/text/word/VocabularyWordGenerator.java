package ru.sbt.tengri.json.message.generator.text.word;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbt.tengri.json.message.generator.text.RandomUtils;

public class VocabularyWordGenerator implements WordGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(VocabularyWordGenerator.class);

  private final List<String> vocabulary;
  private final RandomUtils rndUtils = new RandomUtils();

  public VocabularyWordGenerator(Path vocabularyFilePath) {
    this.vocabulary = loadVocabulary(vocabularyFilePath);
  }

  @Override
  public String generateWord() {
    return rndUtils.getRandomValueFromList(vocabulary);
  }

  private List<String> loadVocabulary(Path vocabularyFilePath) {
    try {
      List<String> vocabulary = Files.lines(vocabularyFilePath)
                                     .flatMap(line -> Arrays.stream(line.split(" ")))
                                     .collect(Collectors.toList());
      LOG.debug("Vocabulary with {} tokens was successfully loaded", vocabulary.size());
      return vocabulary;
    } catch (IOException e) {
      throw new IllegalArgumentException("Invalid vocabulary file", e);
    }
  }

}
