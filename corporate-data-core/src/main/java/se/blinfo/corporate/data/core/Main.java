package se.blinfo.corporate.data.core;

/**
 *
 * @author jel
 */
public class Main {

    public static void main(String[] args) {
        AllaBolag allaBolag = new AllaBolag();
        System.out.println(allaBolag.companyName("556094-6617"));
        System.out.println(allaBolag.phone("556094-6617"));
    }
}
