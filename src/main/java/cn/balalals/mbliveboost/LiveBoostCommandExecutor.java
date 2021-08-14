//package cn.balalals.mbliveboost;
//
//
//import cn.balalals.mbliveboost.websocket.WebSocketClient;
//
//import javax.net.ssl.SSLException;
//
//public class LiveBoostCommandExecutor implements CommandExecutor {
//    private WebSocketClient webSocketClient;
//    @Override
//    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//        if(args.length >= 1) {
//            String subCommand = args[0];
//            System.out.println("subCommand:" + subCommand);
//            if("start".equals(subCommand)) {
//                try {
//                    this.webSocketClient = new WebSocketClient(LiveBoost.roomId);
//                    webSocketClient.open();
//                } catch (SSLException | InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }else if("stop".equals(subCommand)) {
//                try {
//                    webSocketClient.close();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return false;
//    }
//}
