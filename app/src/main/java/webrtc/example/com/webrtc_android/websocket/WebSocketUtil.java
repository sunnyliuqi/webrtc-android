package webrtc.example.com.webrtc_android.websocket;

import android.content.Context;
import android.util.Log;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import webrtc.example.com.webrtc_android.utils.JwtUtil;
import webrtc.example.com.webrtc_android.webrtc.PeerConnectUtil;

import java.io.IOException;

/**
 * websockte 工具类
 */
public class WebSocketUtil {
    private WebSocketUtil() {

    }

    private static String URL = "ws://10.110.1.11:2019/websocket/chat/websocket";
    private static WebSocketUtil webSocketUtil;
    private WebSocket webSocket;
    private String toUserName;

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    /**
     *
     * @return
     */
    public static WebSocketUtil getInstance() {
        if (webSocketUtil == null) {
            webSocketUtil = new WebSocketUtil();
        }
        return webSocketUtil;
    }

    /**
     * 初始化
     * @param context
     */
    public static void init(Context context){
        getInstance().setWebSocket(webSocketUtil.connect(context,URL));
    }

    private WebSocket connect(Context context,String url) {
        try {

            return webSocket = new WebSocketFactory().createSocket(url, 30) //ws地址，和设置超时时间
                    .setFrameQueueSize(5)//设置帧队列最大值为5
                    .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                    .addListener(myWsListener())//添加回调监听
                    .addProtocol(JwtUtil.get(context))
                    .connectAsynchronously();//异步连接
        } catch (IOException e) {
            Log.d("WebSocketClient", "建立websocket 链接");
            return null;
        }
    }

    public void sendMsg(String username, String msg) {
        getWebSocket().sendText("{'username':'" + username + "','message': '" + msg + "'}");
    }

    public void sendSdpMsg( String username, String sdpMsg) {
        getWebSocket().sendText("{\"username\":\"" + username + "\",\"sdpMsg\":true,\"sdpMessage\":" + sdpMsg + "}");
    }

    public void applySdp( String username, String type) {
        getWebSocket().sendText("{'username':'" + username + "','sdpMsg':true,'sdpMessage': {'type':'" + type + "'}}");
    }

    public void disconnect() {
        getWebSocket().disconnect();
    }

    public WebSocketAdapter myWsListener() {
        return new MyWebSocketListener();
    }


    public void dealSdp(SdpMessage sdpMsg) {
        //这个里面处理视频聊天

        if ("call".equals(sdpMsg.getType())) {

        } /*else if (sdpMsg.type == 'permit') {
        receivePermit();
    }*/ else if ("offer".equals(sdpMsg.getType() ) ) {
            PeerConnectUtil.getInstance().receiveOffer(sdpMsg);
        } else if ("answer".equals(sdpMsg.getType() ) ) {
            PeerConnectUtil.getInstance().receiveAnswer(sdpMsg);
        }else if ("deny".equals(sdpMsg.getType() ) ) {

        }
        else if ("hangup".equals(sdpMsg.getType() )) {

        } else if ("candidate".equals(sdpMsg.getType())) {
            PeerConnectUtil.getInstance().setIceCandidate(sdpMsg,PeerConnectUtil.getInstance().getPeerConnection());
        }
    }
}