package xyz.isatimur.knowledgebase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * @author Timur Isachenko {@literal <tisachenko@at-consulting.ru>} on 06.07.2017.
 */
public class CampaignReader {
    private static final Comparator<Map.Entry<String, Long>> COMPARATOR_ENTRY_VALUE =
            Comparator.comparing((Map.Entry<String, Long> e) -> e.getValue());
    private static final Logger LOGGER = LoggerFactory.getLogger(CampaignReader.class.getSimpleName());

    /**
     * This is the entry point of our application.
     *
     * @param args here we should provide two strings paths to our input data
     * @throws IOException if no files 've been found
     */
    public static void main(String[] args) throws IOException {

        String pathToCampaignFile = null;
        String pathToInputFIle = null;
        if (args != null && args.length == 2) {
            pathToCampaignFile = args[0];
            pathToInputFIle = args[1];
        } else {
            pathToCampaignFile = "campaign.txt";
            pathToInputFIle = "input.txt";
        }
        Stream<String> lines = Files.lines(Paths.get(pathToCampaignFile));
        Stream<String> inputLines = Files.lines(Paths.get(pathToInputFIle));
        solution(lines, inputLines)
                .stream()
                .forEach(CampaignReader::logging);
    }

    /**
     * Solution method that gets an input data of files and transform it in a
     * there are segments represent a keys and campaign name - values accordingly.
     *
     * @param lines      initialized campaign stream from file
     * @param inputLines initialized input stream from file
     * @return List of entries
     */
    public static List<Optional<Map.Entry<String, Long>>> solution(Stream<String> lines, Stream<String> inputLines) {
        //initialize and reformat input data of campaigns
        Map<Integer, Set<String>> output = lines.parallel()
                .map((String s) -> {
                    String campaign = s.substring(0, s.indexOf(' '));
                    return Arrays.stream(s.split(" "))
                            .skip(1)
                            .map(Integer::new)
                            .collect(toMap((Integer i) -> i, i -> campaign, (i1, i2) -> i1));
                })
                .flatMap((Map<Integer, String> m) -> m.entrySet().stream())
                .distinct()
                .collect(groupingBy(Map.Entry<Integer, String>::getKey, mapping(Map.Entry<Integer, String>::getValue, toSet())));

        //output all campaign according to README description
        return inputLines
                .map((String str) ->
                        Arrays.stream(str.split(" "))
                                .skip(1)
                                .map(Integer::new)
                                .map(el -> output.get(el) != null ? output.get(el) : null)
                                .flatMap((Set<String> s) -> s != null ? s.stream() : Stream.empty())
                                .collect(groupingBy((String p) -> p, mapping(p1 -> 1, counting())))
                                .entrySet()
                                .stream()
                                .max(COMPARATOR_ENTRY_VALUE)
                ).collect(toList());

    }

    private static void logging(Optional<Map.Entry<String, Long>> e) {
        LOGGER.info(e.map(Map.Entry<String, Long>::getKey).orElse("no campaign"));
    }
}