package dev.isnow.qubolinker.express;

import dev.isnow.qubolinker.QuboServer;
import dev.isnow.qubolinker.util.FileUtil;
import express.Express;
import express.utils.Status;

public class ApiController {

    public ApiController() {
        Express app = new Express("0.0.0.0");

        app.get("/", (req, res) -> res.send("Fuck off you stupid cunt, your ip: " + req.getAddress().getHostAddress()));
        app.post("/hit", (req, res) -> {
            if(req.getHeader("key").get(0).equals("QUBOLINKER-1337")) {
                String hit =req.getHeader("hit").get(0);
                System.out.println(hit);
                FileUtil.writeOutput(hit, QuboServer.getInstance().getScanningFile());
                res.setStatus(Status._200);
                res.send("VALID");
            } else {
                res.setStatus(Status._401);
                res.send("Fuck off you stupid cunt, your ip: " + req.getAddress().getHostAddress());
            }
        });

        app.listen(null, 1338);
    }
}
