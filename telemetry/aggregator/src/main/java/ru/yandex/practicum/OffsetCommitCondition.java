package ru.yandex.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class OffsetCommitCondition {

    @Value("${offset.commit.threshold:10}")
    private static int threshold;

    public static boolean shouldCommit(int count) {
        return count % threshold == 0;
    }
}
