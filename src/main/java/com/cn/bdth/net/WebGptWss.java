package com.cn.bdth.net;

import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 长连接响应. (标准)
 *
 * @author bdth
 */
@Slf4j
/**
 * WebSocket服务端点配置
 * - 使用@ServerEndpoint注解声明WebSocket的访问路径，支持token和model参数
 * - @Service表明该类是Spring服务组件，参与容器管理
 * - @SuppressWarnings("all") 忽略所有编译器警告
 */
@ServerEndpoint("/gpt-web/api/{token}/{model}")
@SuppressWarnings("all")
@Service
public class WebGptWss {

//    private Session session;
//    private static ChatUtils chatUtils;
//    private static GptService gptService;
//    private static ChatGptCommon chatGptCommon;
//    private static ControlCommon controlCommon;
//    private static StringBuilder allMessages = new StringBuilder();
//
//
//    @OnOpen
//    public void onOpen(final Session session, @PathParam("token") String token) {
//        try {
//            assert session.getId() != null;
//            assert StpUtil.getLoginIdByToken(token) != null;
//        } catch (Exception e) {
//            return;
//        }
//        this.session = session;
//        if (gptService == null) {
//            initDependencies();
//        }
//    }
//
//    private void initDependencies() {
//        controlCommon = (ControlCommon) SpringContextUtil.getBean("controlCommon");
//        chatUtils = (ChatUtils) SpringContextUtil.getBean("chatUtils");
//        gptService = (GptService) SpringContextUtil.getBean("gptServiceImpl");
//        chatGptCommon = (ChatGptCommon) SpringContextUtil.getBean("chatGptCommon");
//    }
//
//    @OnMessage
//    public void onMessage(final String message, final @PathParam("token") String token, final @PathParam("model") String model) {
//        try {
//                final GptWebDto gptWebDto = JSONObject.parseObject(message, GptWebDto.class);
//                final Long userId = UserUtils.getLoginIdByToken(token);
//                chatUtils.lastOperationTime(userId);
//                final ChatGptCommon.ChatGptStructure chatGptStructure = chatGptCommon.getChatGptStructure();
//                final String s = chatUtils.drawingCueWord(gptWebDto.getMessages());
//
//                if (s == null) {
//
//                    final Long frequency = controlCommon.getControl().getEnableGptPlus() ? (false ? chatGptStructure.getGptPlusFrequency() : chatGptStructure.getGptFrequency()) : chatGptStructure.getGptFrequency();
//
//                    chatUtils.deplete(frequency, userId);
//                    //与GPT建立通信
//                    gptService.concatenationGpt(chatUtils.conversionStructure(gptWebDto), model, chatGptStructure)
//                            .doFinally(signal -> handleWebSocketCompletion())
//                            .subscribe(data -> {
//                                if (JSON.isValid(data)) {
//                                    JSONObject jsonObject = JSONObject.parseObject(data);
//                                    JSONArray choices = jsonObject.getJSONArray("choices");
//                                    if (choices != null && !choices.isEmpty()) {
//                                        JSONObject delta = choices.getJSONObject(0).getJSONObject("delta");
//                                        if (delta.containsKey("content")) {
//                                            // 可能会抛出关闭异常
//                                            try {
//                                                this.session.getBasicRemote().sendText(delta.getString("content"));
//                                                this.allMessages.append(delta.getString("content"));
//                                            } catch (Exception e) {
//                                                //用户可能手动端口连接
//                                                throw new CloseException();
//                                            }
//                                        }
//                                    }
//                                }
//                            }, throwable -> {
//                                //为 Close异常时 过滤
//                                if (!(throwable instanceof CloseException)) {
//                                    chatUtils.compensate(frequency, userId);
//                                    String result = this.allMessages.toString().replaceAll("\n", "");
//                                    log.info(result);
//                                    Dialogue dialogue =new Dialogue()
//                                            .setMessage(gptWebDto.getMessages().get(gptWebDto.getMessages().size()-1).getContent().replaceAll("\n",""))
//                                            .setUserId(userId)
//                                            .setContent(result);
//                                    try {
//                                        gptService.putDialogue(dialogue);
//                                    } catch (Exception e) {
//                                        log.warn("写入数据库异常", e);
//                                        throw new RuntimeException(e);
//                                    }
//                                    log.error("调用GPT时出现异常 异常信息:{} ", throwable.getMessage());
//                                    appointSendingSystem(ExceptionMessages.GPT_TIMEOUT);
//                                }
//                            });
//                } else {
//                    Long frequency = null;
//                    try {
//                        frequency = chatGptStructure.getGptTextImageFrequency();
//                        chatUtils.deplete(frequency, userId);
//                        final String context = gptService.drawAccordingGpt(s, chatGptStructure);
//                        this.session.getBasicRemote().sendText(context);
//                    } catch (DrawingException e) {
//                        chatUtils.compensate(frequency, userId);
//                        appointSendingSystem(ExceptionMessages.GPT_TIMEOUT);
//                    } finally {
//                        handleWebSocketCompletion();
//                    }
//                }
//        } catch (FrequencyException e) {
//            appointSendingSystem(e.getMessage());
//            handleWebSocketCompletion();
//        } catch (Exception e) {
//            log.error(" 与 OPEN Ai建立连接失败 原因:{}", e.getMessage());
//            appointSendingSystem(ExceptionMessages.GPT_ERR);
//            handleWebSocketCompletion();
//        }
//    }
//
//    @OnClose
//    public void handleWebSocketCompletion() {
//        try {
//            this.session.close();
//        } catch (IOException e) {
//            log.error("关闭 GPT WebSocket 会话失败.", e);
//        }
//    }
//
//    @OnError
//    public void onError(Session session, Throwable throwable) {
//        log.warn("GPT websocket出现异常 原因:{}", throwable.getMessage());
//        //打印堆栈
//        //      throwable.printStackTrace();
//    }
//
//    public void appointSendingSystem(final String message) {
//        try {
//            this.session.getBasicRemote().sendText(message);
//        } catch (Exception e) {
//
//        }
//    }


}
