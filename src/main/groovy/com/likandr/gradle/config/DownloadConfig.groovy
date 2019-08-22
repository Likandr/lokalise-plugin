package com.likandr.gradle.config

class DownloadConfig {
    boolean originalFilenames = false
    Order order = Order.FIRST_ADDED
    Strategy emptyTranslationStrategy = Strategy.SKIP

    enum Order {
        FIRST_ADDED("first_added"),
        LAST_ADDED("last_added"),
        LAST_UPDATED("last_updated"),
        A_Z("a_z"),
        Z_A("z_a")

        final String value

        private Order(String value) {
            this.value = value
        }
    }

    enum Strategy {
        KEEP_EMPTY("empty"),
        REPLACE_WITH_BASE_LANGUAGE("base"),
        SKIP("skip")

        final String value

        private Strategy(String value) {
            this.value = value
        }
    }
}
