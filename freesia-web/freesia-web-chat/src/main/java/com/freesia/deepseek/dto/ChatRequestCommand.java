package com.freesia.deepseek.dto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description Deepseek 对话请求 DTO
 * @date 2026-07-19
 */
public class ChatRequestCommand {

    private String prompt;
    private List<Message> messages;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public static class Message {
        private String role;
        private String content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
