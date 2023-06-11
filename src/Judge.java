import java.util.ArrayList;

public class Judge {
    public static boolean judge(ArrayList<String> judgeOutput, ArrayList<String> userOutput) {
        if (userOutput.size() != judgeOutput.size()) return false;

        for (int i = 0; i < judgeOutput.size(); i++) {
            if (!judgeOutput.get(i).equals(userOutput.get(i))) return false;
        }

        return true;
    }

    public static void prepareStrings(ArrayList<String> output) {
        while (output.size() > 0 && output.get(0).matches("\\s*")) {
            output.remove(0);
        }

        while (output.size() > 0 && output.get(output.size() - 1).matches("\\s*")) {
            output.remove(output.size() - 1);
        }

        output.replaceAll(Judge::trimEnd);
    }

    private static String trimEnd(String string) {
        int index = string.length() - 1;
        while (index >= 0 && Character.isWhitespace(string.charAt(index))) index--;
        return string.substring(0, index + 1);
    }
}
