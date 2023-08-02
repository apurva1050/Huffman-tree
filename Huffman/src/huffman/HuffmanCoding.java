package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;

    /**
     * Constructor used by the driver, sets filename
     * DO NOT EDIT
     * @param f The file we want to encode
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }

    /**
     * Reads from filename character by character, and sets sortedCharFreqList
     * to a new ArrayList of CharFreq objects with frequency > 0, sorted by frequency
     */
    public void makeSortedList() {
        StdIn.setFile(fileName);
        this.sortedCharFreqList = new ArrayList<CharFreq>();
        int[] arr = new int[128];
        double count = 0; //for total number of elements in the text file
        //StdIn.setFile(fileName);
        while(StdIn.hasNextChar()){
           char i = StdIn.readChar();
           arr[i]= arr[i]+1;
           count = count+1; 
        }
        for(int i = 0; i < arr.length; i++){
            if(arr[i]!=0){
                CharFreq charfreq = new CharFreq((char)i, arr[i]/count);
                sortedCharFreqList.add(charfreq);
            }
        }
        if(sortedCharFreqList.size()==1){
            CharFreq charfreq = sortedCharFreqList.get(0);
            char i = charfreq.getCharacter();
            int b = i+1;
            if(i+1 == 128){
                int c = 0;
                CharFreq charfreq1 = new CharFreq((char)c, 0);
                sortedCharFreqList.add(charfreq1);
            }else{
                CharFreq charfreq1 = new CharFreq((char)b, 0);
                sortedCharFreqList.add(charfreq1);
            }
            //Collections.sort(sortedCharFreqList);
        }
        Collections.sort(sortedCharFreqList);
        
        
	
    }

    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    public void makeTree() {
        //huffman root is root of tree
        Queue<TreeNode> source = new Queue<TreeNode>();
        Queue<TreeNode> target = new Queue<TreeNode>();
        Queue<TreeNode> deletedNodes = new Queue<TreeNode>();

        for(int i = 0; i <sortedCharFreqList.size(); i++){
            TreeNode t = new TreeNode(sortedCharFreqList.get(i), null, null);
            source.enqueue(t);
        }
        while(!source.isEmpty() ||  target.size()!=1){
          //if(target.isEmpty() || deletedNodes.size() < 2){
            
            while(deletedNodes.size() < 2){
                if(target.isEmpty()){
            deletedNodes.enqueue(source.dequeue());
            }
            else{
                  if(!source.isEmpty()){
                    TreeNode frontSource = source.peek();
                    TreeNode frontTarget = target.peek();
                    if(frontSource.getData().getProbOcc() <= frontTarget.getData().getProbOcc()){
                        deletedNodes.enqueue(source.dequeue());
                    }else{
                        deletedNodes.enqueue(target.dequeue());
                    }
                  }else{{
                    if(source.isEmpty()){
                        deletedNodes.enqueue(target.dequeue());
                    }
                  }}  
                

                /*TreeNode frontSource = source.peek();
                TreeNode frontTarget = target.peek();
                if(frontSource.getData().getProbOcc() <= frontTarget.getData().getProbOcc()){
                    deletedNodes.enqueue(source.dequeue());
                }else{
                    deletedNodes.enqueue(target.dequeue());
                }*/
            }
            }   
        
        //while(!deletedNodes.isEmpty()){
        TreeNode t1 = new TreeNode();
        TreeNode t2 = new TreeNode();
        double d1;
        if(deletedNodes.isEmpty()){
             t1 = null;
             t2 = null;
             d1 = 0;
        }else{
             t1 = deletedNodes.dequeue();
             t2 = deletedNodes.dequeue();
             d1 = t1.getData().getProbOcc() + t2.getData().getProbOcc();
        }
        //double d1 = t1.getData().getProbOcc() + t2.getData().getProbOcc();
        CharFreq newChar = new CharFreq(null, d1);
        TreeNode n = new TreeNode(newChar, t1, t2);
        
        target.enqueue(n);
    }
        
huffmanRoot = target.dequeue();






} 

    private void preorder(Character c, TreeNode node, String s){
        if(c == null|| node == null){
            return;
        }
        if(node.getData().getCharacter() == c){
            encodings[c] = s;
            
        }
        preorder(c, node.getLeft(), s+0);
        preorder(c,node.getRight(), s+1);
    }


  

    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */
    public void makeEncodings() {
        encodings = new String[128];       //the difference between regular encodings and String[] encodings?
        String a = "";
        
        for(int i = 0; i < sortedCharFreqList.size(); i++){
            preorder(sortedCharFreqList.get(i).getCharacter(),huffmanRoot, a);
        }
        
        


    }

    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);
        String a = "";
        while(StdIn.hasNextChar()){
            Character c = StdIn.readChar();
            a = a + encodings[c];
        }
        writeBitString(encodedFile, a);
    }
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * DO NOT EDIT
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Using a given encoded file name, this method makes use of the readBitString method 
     * to convert the file into a bit string, then decodes the bit string using the 
     * tree, and writes it to a decoded file. 
     * 
     * @param encodedFile The file which has already been encoded by encode()
     * @param decodedFile The name of the new file we want to decode into
     */

    /*private Character pre(TreeNode node, Character c){
        if(node.getLeft()==null &&  node.getRight()== null){
            return node.getData().getCharacter();
        }

        if(c==0){
            pre(node.getLeft(), c);
        }
    }*/





    public void decode(String encodedFile, String decodedFile) {
        StdOut.setFile(decodedFile);
        String a = readBitString(encodedFile);
        TreeNode temp = huffmanRoot;
        for(int i =0; i < a.length();i++){
            if(temp.getData().getCharacter()!=null){
                StdOut.print(temp.getData().getCharacter());
                temp = huffmanRoot;
            }
            if(a.charAt(i)=='0'){
                temp = temp.getLeft();
            }
            else{
                if(a.charAt(i)=='1'){
                temp = temp.getRight();
            }
        }
        if(temp.getData().getCharacter()!=null){
            StdOut.print(temp.getData().getCharacter());
            temp = huffmanRoot;
        }
        }







        //StdIn.setFile(encodedFile);
     
         /*String a = readBitString(encodedFile);
        TreeNode temp = huffmanRoot;
        
        a = a +' ';
        
        while(!a.isEmpty()){
            //for(int i = 0; i < a.length()-1; i++){
            if(temp.getData().getCharacter()!=null){
                StdOut.print(temp.getData().getCharacter());
                temp=huffmanRoot;
            }
            if(a.charAt(0)=='0'){
                temp = temp.getLeft();
                a=a.substring(1);
            }else
                if(a.charAt(0)=='1'){
                    temp=temp.getRight();
                    a=a.substring(1);
                }
            }
            */
        }
            
        
        

        
     /*   String a = readBitString(encodedFile);
        String s = "";
        TreeNode ptr = huffmanRoot;
        int i=0;
        TreeNode x =ptr;
        while(!a.isEmpty() && i < a.length()){
            
            if(a.charAt(i)=='0'){
                if(x.getLeft().getData().getCharacter()!=null){
                    s=s+x.getLeft().getData().getCharacter();
                    i++;
                  a=  a.substring(i);
                    x=ptr;
                    //i++;
                }else{
                    x=x.getLeft();
                    i++;
                    //ptr = ptr.getLeft();
                    a=a.substring(i);
                    
                }
            }else{if(a.charAt(i)=='1'){
               
                    if(x.getRight().getData().getCharacter()!=null){
                        s=s+x.getRight().getData().getCharacter();
                        i++;
                       a= a.substring(i);
                        x=ptr;
                        //i++;
                    }else{
                        x=x.getRight();
                        i++;
                        //ptr=ptr.getRight();
                        a=a.substring(i);
                    }
                }
                
            }
        }
        StdOut.print(s);*/
        
    
    

    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * DO NOT EDIT
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /*
     * Getters used by the driver. 
     * DO NOT EDIT or REMOVE
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }
}
