package net.wanho.fitai.modules.media.service;

import java.util.Map;

public interface VodCallbackServiceI {

    void handleCallback(Map<String, Object> payload);
}
