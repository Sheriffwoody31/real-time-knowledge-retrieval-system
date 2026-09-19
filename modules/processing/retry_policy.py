import random
from datetime import datetime, timedelta, timezone


class ExponentialBackoffRetryPolicy:
    def __init__(
        self,
        *,
        base_delay_seconds: int = 60,
        max_delay_seconds: int = 3600,
        jitter_seconds: int = 30,
    ) -> None:
        self.base_delay_seconds = base_delay_seconds
        self.max_delay_seconds = max_delay_seconds
        self.jitter_seconds = jitter_seconds

    def next_attempt_at(self, attempts: int) -> datetime:
        delay = min(self.base_delay_seconds * (2**max(attempts - 1, 0)), self.max_delay_seconds)
        jitter = random.randint(0, self.jitter_seconds)
        return datetime.now(timezone.utc) + timedelta(seconds=delay + jitter)

