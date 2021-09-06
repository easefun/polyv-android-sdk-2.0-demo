package com.easefun.polyvsdk.player.knowledge.vo;

import java.util.List;

/**
 * @author suhongtao
 */
public class PolyvPlayerKnowledgeVO {

    private Boolean fullScreenStyle = false;
    private List<WordType> wordTypes;

    public Boolean getFullScreenStyle() {
        return fullScreenStyle;
    }

    public void setFullScreenStyle(Boolean fullScreenStyle) {
        this.fullScreenStyle = fullScreenStyle;
    }

    public List<WordType> getWordTypes() {
        return wordTypes;
    }

    public void setWordTypes(List<WordType> wordTypes) {
        this.wordTypes = wordTypes;
    }

    public static class WordType {
        private String name;
        private List<WordKey> wordKeys;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<WordKey> getWordKeys() {
            return wordKeys;
        }

        public void setWordKeys(List<WordKey> wordKeys) {
            this.wordKeys = wordKeys;
        }

        public static class WordKey {
            private String name;
            private List<KnowledgePoint> knowledgePoints;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<KnowledgePoint> getKnowledgePoints() {
                return knowledgePoints;
            }

            public void setKnowledgePoints(List<KnowledgePoint> knowledgePoints) {
                this.knowledgePoints = knowledgePoints;
            }

            public static class KnowledgePoint {
                private String name;
                private Integer time;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public Integer getTime() {
                    return time;
                }

                public void setTime(Integer time) {
                    this.time = time;
                }
            }
        }

    }

}
