import java.util.ArrayList;

public class TextUI {
    
    private Board board;
    
    public TextUI(Board board) {
        this.board = board;
    }
    
    
    public static void print() {
        int iHPad = 4; // minimum of 2
        String sHPad = "    ";
        int iVPad = 1;
        String sVPad = "\n";
        
        ArrayList<String> output = new ArrayList<String>();
        
        output.add(sVPad);
        output.add(sHPad + "ssssssssssssssssss______\n");
        output.add(sHPad + "sssssssssssssssss/TTssTT\\\n");
        output.add(sHPad + "ssssssssss______/sssTTsss\\______\n");
        output.add(sHPad + "sssssssss/TTssTT\\sssTTsss/TTssTT\\\n");
        output.add(sHPad + "ss______/sssTTsss\\______/sssTTsss\\______\n");
        output.add(sHPad + "s/TTssTT\\sssTTsss/TTssTT\\sssTTsss/TTssTT\\\n");
        output.add(sHPad + "/sssTTsss\\______/sssTTsss\\______/sssTTsss\\\n");
        output.add(sHPad + "\b\bTT\\sssTTsss/TTssTT\\sssTTsss/TTssTT\\sssTTsss/\n");
        output.add(sHPad + "s\\______/sssTTsss\\______/sssTTsss\\______/\n");
        output.add(sHPad + "s/TTssTT\\sssTTsss/TTssTT\\sssTTsss/TTssTT\\\n");
        output.add(sHPad + "/sssTTsss\\______/sssTTsss\\______/sssTTsss\\\n");
        output.add(sHPad + "\\sssTTsss/TTssTT\\sssTTsss/TTssTT\\sssTTsss/\n");
        output.add(sHPad + "s\\______/sssTTsss\\______/sssTTsss\\______/\n");
        output.add(sHPad + "s/TTssTT\\sssTTsss/TTssTT\\sssTTsss/TTssTT\\\n");
        output.add(sHPad + "/sssTTsss\\______/sssTTsss\\______/sssTTsss\\\n");
        output.add(sHPad + "\\sssTTsss/TTssTT\\sssTTsss/TTssTT\\sssTTsss/\n");
        output.add(sHPad + "s\\______/sssTTsss\\______/sssTTsss\\______/\n");
        output.add(sHPad + "ssTTssTT\\sssTTsss/TTssTT\\sssTTsss/TTssTT\n");
        output.add(sHPad + "sssssssss\\______/sssTTsss\\______/\n");
        output.add(sHPad + "ssssssssssTTssTT\\sssTTsss/TTssTT\n");
        output.add(sHPad + "sssssssssssssssss\\______/\n");
        output.add(sHPad + "ssssssssssssssssssTTssTT\n");
        
        for (int i = 0; i < output.size(); i++) {
            System.out.print(output.get(i));
        }
    }
    
    public static void main(String[] args) {
        TextUI.print();
    }
}