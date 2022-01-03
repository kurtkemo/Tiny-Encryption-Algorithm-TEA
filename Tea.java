
/**
 * Tiny Encryption Algorithm
 *
 * @author (Kurt Johnson)
 * @version (2/21/21)
 * 
 * Part 3:
 * Ciphertext from the previous block is used to affect the second block and so on. 
 * The resulting ciphertext is then xored with the proceeding message and make that the input of the encryption function in our case TEA.
 * The final ciphertext value is all the encrypted messages xored with the previous ciphertext. 
 * Input of the encryption function(TEA) will be the previous ciphertext xored with the proceeding message. First run gets IV.
 */
import java.math.*;
import java.util.*;
public class Tea {

    static long key[] = new long[4];
    static long plainText[] = new long[2];
    public static void main(String[] args) 
    {
        Tea t1 = new Tea();

        System.out.println("Enter 128-bit key: ");
        String hexKey = t1.hexKey();//gets user input

        System.out.println("Enter plaintext: ");
        String plain = t1.plainText();//gets user input

        //if the plaintext is less than 64 binary digits long then we add 0's to the string
        if(plain.length() < 64)
        {
            StringBuilder sb = new StringBuilder();
            int diff = 64 - plain.length();
            for(int i = 0; i < diff; i++)
            {
                sb.append("0");
            }
            plain = sb + plain;
        }
        //puts the strings into string variables
        String hex0 = hexKey.substring(0,32);
        String hex1 = hexKey.substring(32,64);
        String hex2 = hexKey.substring(64,96);
        String hex3 = hexKey.substring(96,128);
        String plain0 = plain.substring(0,32);
        String plain1 = plain.substring(32,64);
        //puts string variables into long arrays
        key[0] = Long.parseLong(hex0, 2);
        key[1] = Long.parseLong(hex1, 2);
        key[2] = Long.parseLong(hex2, 2);
        key[3] = Long.parseLong(hex3, 2);
        plainText[0] = Long.parseLong(plain0, 2);
        plainText[1] = Long.parseLong(plain1, 2);

        //encrypt and decrypt methods
        t1.encrypt();
        t1.decrypt();
    }

    //returns binary string representation of user inputted hex values
    public String hexToBin(String hex)
    {

        String bin = "";
        String binPart = "";
        int iHex;
        hex = hex.trim();
        hex = hex.replaceFirst("0x", "");//replaces the beginning of the hex "0x" with nothing.

        for(int i = 0; i < hex.length(); i++)
        {
            iHex = Integer.parseInt(""+hex.charAt(i),16);
            binPart = Integer.toBinaryString(iHex);

            while(binPart.length() < 4)
            {
                binPart = "0" + binPart;
            }
            bin += binPart;
        }

        return bin;
    }

    public void encrypt()
    {
        long sum = 0;
        long delta = 0x9e3779b9;
        long L = plainText[0];
        long R = plainText[1];

        for (int i=1; i<=32; i++) 
        {
            sum += delta;
            L = (L + (((R << 4)+key[0]) ^ (R + sum) ^ ((R >> 5)+key[1])) & 0xffffffffL);//& 0xffffffffL trims long to length of 32 bits 
            R = (R + (((L << 4)+key[2]) ^ (L + sum) ^ ((L >> 5)+key[3])) & 0xffffffffL);
        }

        String hexStr = Long.toString(L,16);//print hex of the left half
        String hexStrR = Long.toString(R,16);//print hex of the right half
        System.out.println("Ciphertext: 0x" + hexStr.toUpperCase() + hexStrR.toUpperCase());

        plainText[0] = L;
        plainText[1] = R;
    }

    public void decrypt()
    {

        long delta = 0x9e3779b9;
        long sum = delta << 5;
        long L = plainText[0];
        long R = plainText[1];
        for (int i=1; i<=32; i++) 
        {

            R = (R - (((L << 4)+key[2]) ^ (L + sum) ^ ((L >> 5))+key[3]) & 0xffffffffL);//& 0xffffffffL trims long to length of 32 bits
            L = (L - (((R << 4)+key[0]) ^ (R + sum) ^ ((R >> 5))+key[1]) & 0xffffffffL);
            sum = sum - delta;
        }

        String LR = Long.toBinaryString(L)+Long.toBinaryString(R);
        long decimal = Long.parseLong(LR, 2);
        String hexStr = Long.toString(decimal,16);//print hex
        System.out.println("Decrypted text: 0x" + hexStr.toUpperCase());
    }

    public String hexKey()
    {
        //Takes user input and returns binary String that is the hex key
        Scanner scan = new Scanner(System.in);
        String hexKey = scan.nextLine();
        String binaryStringHexKey = hexToBin(hexKey);

        return binaryStringHexKey;
    }

    public String plainText()
    {
        //returns the binary string that is the plaintext block
        Scanner scan = new Scanner(System.in);
        String plainText = scan.nextLine();
        String binaryStringPlainText = hexToBin(plainText);

        return binaryStringPlainText;
    }
}