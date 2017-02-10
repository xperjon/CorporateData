package se.xperjon.corporate.data.api;

import se.blinfo.corporate.data.core.AllaBolag;
import static spark.Spark.get;

/**
 *
 * @author jel
 */
public class Main {

    private static final AllaBolag SERVICE = new AllaBolag();

    public static void main(String[] args) {
        get("/company/:cin", (req, res) -> {
            return SERVICE.companyName(req.params(":cin"));
        });
    }

}
