from app.config import AppConfig


class AppContainer:
    def __init__(self, config: AppConfig) -> None:
        self.config = config


def build_container(config: AppConfig) -> AppContainer:
    return AppContainer(config=config)

