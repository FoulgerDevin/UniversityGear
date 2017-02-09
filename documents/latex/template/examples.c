/*
 * LINUX CODE
 */
//For creating threads in linux
	clone(CLONE_VM | CLONE_FS | CLONE_FILES | CLONE_SIGHAND, 0);

//For forking in linux
	clone(SIGCHILD, 0);

/*
 * WINDOWS CODE
 */
 //EPROCESS
	Pcb 			: _KPROCESS
	ProcessLock 		: _EX_PUSH_LOCK
	CreateTime 		: _LARGE_INTEGER
	ExitTime 		: _LARGE_INTEGER
	RundownProtect 		: _EX_RUNDOWN_REF
	UniqueProcessId 	: Ptr32 Void

	ObjectTable 		: Ptr32 _HANDLE_TABLE
	Token 			: _EX_FAST_REF

	Win32Process 		: Ptr32 Void
	Job 			: Ptr32 _EJOB

	TimerResolutionLink 		: _LIST_ENTRY
	RequestedTimerResolution 	: Uint4B
	ActiveThreadsHighWatermark 	: Uint4B
	SmallestTimerResolution 	: Uint4B
	TimerResolutionStackRecord 	: Ptr32 _PO_DIAG_STACK_RECORD

//Interrupt handler for Linux
	int request_irq(unsigned int irq,
		irq_handler_t handler,
		unsigned long flags,
		const char *name,
		void *dev)

//Linux page structure
	struct page {
		unsigned long flags;
		atomic_t _count;
		atomic_t _mapcount;
		unsigned long private;
		struct address_space *mapping;
		pgoff_t index;
		struct list_head lru;
		void *virtual;
	};

//How to create a space for shared memory in FreeBSD
	void *addr = mmap( void *addr, /* base address */ 
		size_t len, /* length of region */ 
		int prot, /* protection of region */ 
		int flags, /* mapping flags */
		int fd, /* file to map */ 
		off_t offset); /* offset to begin mapping */
	
//Example of user writing to a filesystem Linux
	ret = write (fd, buf, len);

//Filesystem struct in Linux
	struct fs_struct {
		int users; /* user count */
		rwlock_t lock; /* per-structure lock */
		int umask; /* umask */
		int in_exec; /* currently executing a file */
		struct path root; /* root directory */
		struct path pwd; /* current working directory */
	};
