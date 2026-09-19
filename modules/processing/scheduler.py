from modules.processing.dispatcher import ProcessingDispatchService


class ProcessingDispatchScheduler:
    def __init__(
        self,
        dispatch_service: ProcessingDispatchService,
        interval_minutes: int = 15,
    ) -> None:
        self.dispatch_service = dispatch_service
        self.interval_minutes = interval_minutes

    def run_once(self, *, limit: int = 100) -> int:
        return self.dispatch_service.dispatch_created_documents(limit=limit)

