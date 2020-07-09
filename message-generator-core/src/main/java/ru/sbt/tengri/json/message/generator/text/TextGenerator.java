package ru.sbt.tengri.json.message.generator.text;

import ru.sbt.tengri.json.message.generator.text.word.WordGenerator;

public class TextGenerator {

    private final RandomUtils rndUtils = new RandomUtils();
    private final WordGenerator wordGenerator;
    private final Range textWordsAmountRange;

    public TextGenerator(WordGenerator wordGenerator, Range textWordsAmountRange) {
        this.wordGenerator = wordGenerator;
        this.textWordsAmountRange = textWordsAmountRange;
    }

    public String generateText() {
        final int textLength = rndUtils.genRandomValueAtRange(textWordsAmountRange);
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < textLength; i++) {
            final String word = wordGenerator.generateWord();
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(word);
        }
        return sb.toString();
    }

}
