package com.lj.iot.common.aiui.core.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mz
 * @Date 2022/8/9
 * @since 1.0.0
 */
public class AIUIResultVo {

    private String version = "2.1";
    private Response response = new Response();

    public static AIUIResultVo INSTANCE(String text) {
        AIUIResultVo aiuiResultVo = new AIUIResultVo();
        aiuiResultVo.response.outputSpeech.setText(text);
        return aiuiResultVo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    class Response {
        public Response() {
        }

        private OutputSpeech outputSpeech = new OutputSpeech();
        private Boolean shouldEndSession = true;

        public OutputSpeech getOutputSpeech() {
            return outputSpeech;
        }

        public void setOutputSpeech(OutputSpeech outputSpeech) {
            this.outputSpeech = outputSpeech;
        }

        public Boolean getShouldEndSession() {
            return shouldEndSession;
        }

        public void setShouldEndSession(Boolean shouldEndSession) {
            this.shouldEndSession = shouldEndSession;
        }
    }

    class OutputSpeech {
        public OutputSpeech() {
        }

        private String type;
        private String text;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
