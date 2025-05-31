package com.freesia.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Evad.Wu
 * @Description Redisson可读面板属性 数据传输类
 * @date 2023-12-26
 */
@Data
public class RedissonPropertiesDto {
    private String ioThreadedReadsProcessed;
    private String trackingClients;
    @Schema(description = "Redis 服务器启动后的秒数")
    @JsonAlias(value = "uptime_in_seconds")
    private String uptimeInSeconds;
    @Schema(description = "从网络读取的总字节数")
    @JsonAlias(value = "total_net_input_bytes")
    private String totalNetInputBytes;
    private String rss_overhead_ratio;
    private String mem_cluster_links;
    @Schema(description = "服务器的当前频率设置")
    @JsonAlias(value = "hz")
    private String hz;
    private String current_cow_size_age;
    @Schema(description = "构建 ID")
    @JsonAlias(value = "redis_build_id")
    private String redisBuildId;
    private String aof_last_bgrewrite_status;
    @Schema(description = "Redis 使用的事件循环机制")
    @JsonAlias(value = "multiplexing_api")
    private String multiplexingApi;
    private String client_recent_max_output_buffer;
    @Schema(description = "分配器中居民（RSS）的总字节数，包括可以通过MEMORY PURGE释放给操作系统（或只是等待）的页面")
    @JsonAlias(value = "allocator_resident")
    private String allocatorResident;
    private String evicted_clients;
    private String mem_fragmentation_bytes;
    private String repl_backlog_first_byte_offset;
    private String tracking_total_prefixes;
    @Schema(description = "服务器的模式 standalone/sentinel/cluster")
    @JsonAlias(value = "redis_mode")
    private String redisMode;
    private String redis_git_dirty;
    private String current_cow_peak;
    @Schema(description = "allocator_resident和allocator_active之间的差异")
    @JsonAlias(value = "allocator_rss_bytes")
    private String allocatorRssBytes;
    private String repl_backlog_histlen;
    private String io_threads_active;
    @Schema(description = "used_memory_rss（进程RSS）与allocator_resident之间的差异")
    @JsonAlias(value = "rss_overhead_bytes")
    private String rssOverheadBytes;
    private String total_system_memory;
    private String loading;
    private String evicted_keys;
    @Schema(description = "配置指令的值，这是 connected_clients、connected_slaves 和 cluster_connections 的上限")
    @JsonAlias(value = "maxclients")
    private String maxClients;
    private String cluster_enabled;
    @Schema(description = "Redis版本")
    @JsonAlias(value = "redis_version")
    private String redisVersion;
    private String repl_backlog_active;
    private String mem_aof_buffer;
    @Schema(description = "allocator_active和allocator_allocated之间的差异")
    @JsonAlias(value = "allocator_frag_bytes")
    private String allocatorFragBytes;
    @Schema(description = "自启动以来执行的RDB快照数")
    @JsonAlias(value = "rdb_saves")
    private String rdb_saves;
    private String io_threaded_writes_processed;
    @Schema(description = "每秒钟执行的Redis命令数量")
    @JsonAlias(value = "instantaneous_ops_per_sec")
    private String instantaneousOpsPerSec;
    @Schema(description = "Redis分配的字节数，使用其分配器（标准libc、jemalloc或其他分配器，例如tcmalloc）")
    @JsonAlias(value = "used_memory_human")
    private String usedMemoryHuman;
    private String total_error_replies;
    @Schema(description = "角色 master/slave")
    @JsonAlias(value = "role")
    private String role;
    private String current_active_defrag_time;
    @Schema(description = "最大使用内存量")
    @JsonAlias(value = "maxmemory")
    private String maxMemory;
    @Schema(description = "Lua引擎使用的字节数")
    @JsonAlias(value = "used_memory_lua")
    private String usedMemoryLua;
    private String async_loading;
    private String rdb_current_bgsave_time_sec;
    @Schema(description = "Redis在启动时初始消耗的内存量（以字节为单位）")
    @JsonAlias(value = "used_memory_startup")
    private String usedMemoryStartup;
    private String lazyfree_pending_objects;
    @Schema(description = "used_memory_dataset相对于净内存使用量（used_memory减去used_memory_startup）的百分比")
    @JsonAlias(value = "used_memory_startup")
    private String usedMemoryDatasetPerc;
    private String used_memory_vm_eval;
    @Schema(description = "allocator_active和allocator_allocated之间的比率，真正的（外部）碎片度量标准")
    @JsonAlias(value = "allocator_frag_ratio")
    private String allocatorFragRatio;
    @Schema(description = "体系结构（32 或 64 位）")
    @JsonAlias(value = "arch_bits")
    private String archBits;
    @Schema(description = "普通客户端使用的内存")
    @JsonAlias(value = "mem_clients_normal")
    private String memClientsNormal;
    private String expired_time_cap_reached_count;
    private String unexpected_error_replies;
    @Schema(description = "used_memory_rss与used_memory之间的比率。这不仅包括碎片，还包括其他进程开销（请参见allocator_*指标），以及代码、共享库、堆栈等开销")
    @JsonAlias(value = "mem_fragmentation_ratio")
    private String memFragmentationRatio;
    @Schema(description = "上次AOF重写操作的持续时间（以秒为单位）")
    @JsonAlias(value = "aof_last_rewrite_time_sec")
    private String aofLastRewriteTimeSec;
    private String master_replid;
    private String aof_rewrite_in_progress;
    @Schema(description = "每分钟递增的时钟，用于 LRU 管理")
    @JsonAlias(value = "lru_clock")
    private String lruClock;
    @Schema(description = "maxmemory-policy配置指令的值")
    @JsonAlias(value = "maxmemory_policy")
    private String maxMemoryPolicy;
    @Schema(description = "随机值，用于标识 Redis 服务器（用于 Sentinel 和 Cluster）")
    @JsonAlias(value = "run_id")
    private String runId;
    private String latest_fork_usec;
    private String tracking_total_items;
    @Schema(description = "服务器处理的命令总数")
    @JsonAlias(value = "total_commands_processed")
    private String totalCommandsProcessed;
    @Schema(description = "键过期事件的总数")
    @JsonAlias(value = "expired_keys")
    private String expiredKeys;
    @Schema(description = "Redis分配的字节数，使用其分配器（标准libc、jemalloc或其他分配器，例如tcmalloc）")
    @JsonAlias(value = "used_memory")
    private String usedMemory;
    private String module_fork_in_progress;
    private String dump_payload_sanitizations;
    private String mem_clients_slaves;
    @Schema(description = "在主字典中查找键失败的次数")
    @JsonAlias(value = "keyspace_misses")
    private String keyspaceMisses;
    private String server_time_usec;
    private String total_eviction_exceeded_time;
    private String number_of_functions;
    private String lazyfreed_objects;
    private String db0;
    @Schema(description = "Redis消耗的最大内存峰值（以字节为单位）")
    @JsonAlias(value = "used_memory_peak_human")
    private String usedMemoryPeakHuman;
    @Schema(description = "Redis消耗的虚拟内存（以字节为单位）")
    @JsonAlias(value = "used_memory_vm_total")
    private String usedMemoryVmTotal;
    @Schema(description = "在主字典中查找键成功的次数")
    @JsonAlias(value = "keyspace_hits")
    private String keyspaceHits;
    private String rdb_last_cow_size;
    @Schema(description = "服务器为管理其内部数据结构分配的所有开销之和")
    @JsonAlias(value = "used_memory_overhead")
    private String usedMemoryOverhead;
    private String active_defrag_hits;
    @Schema(description = "TCP/IP 监听端口")
    @JsonAlias(value = "tcp_port")
    private String tcpPort;
    @Schema(description = "运行天数")
    @JsonAlias(value = "uptime_in_days")
    private String uptimeInDays;
    private String used_memory_scripts_eval;
    private String used_memory_vm_functions;
    @Schema(description = "used_memory_peak相对于used_memory的百分比")
    @JsonAlias(value = "used_memory_peak_perc")
    private String usedMemoryPeakPerc;
    private String current_save_keys_processed;
    private String blocked_clients;
    @Schema(description = "已处理的读事件总数")
    @JsonAlias(value = "totalReads_processed")
    private String totalReadsProcessed;
    @Schema(description = "在活动到期周期上花费的累计时间")
    @JsonAlias(value = "expire_cycle_cpu_milliseconds")
    private String expireCycleCpuMilliseconds;
    private String sync_partial_err;
    @Schema(description = "缓存的Lua脚本使用的字节数")
    @JsonAlias(value = "used_memory_scripts_human")
    private String usedMemoryScriptsHuman;
    private String aof_current_rewrite_time_sec;
    @Schema(description = "是否开启AOF")
    @JsonAlias(value = "aof_enabled")
    private String aofEnabled;
    @Schema(description = "监控系统（upstart、systemd、unknown或no）")
    @JsonAlias(value = "process_supervised")
    private String processSupervised;
    private String master_repl_offset;
    @Schema(description = "数据集的大小（用于覆盖used_memory_overhead）")
    @JsonAlias(value = "process_supervised")
    private String usedMemoryDataset;
    @Schema(description = "Redis 服务器消耗的用户 CPU，是进程中所有线程（主线程和后台线程）消耗的用户 CPU 之和")
    @JsonAlias(value = "used_cpu_user")
    private String usedCpuUser;
    @Schema(description = "RDB是否成功")
    @JsonAlias(value = "rdb_last_bgsave_status")
    private String rdbLastBgsaveStatus;
    private String tracking_total_keys;
    @Schema(description = "Redis 使用的 Atomicvar API")
    @JsonAlias(value = "atomicvar_api")
    private String atomicvarApi;
    @Schema(description = "allocator_resident和allocator_active之间的比率。这通常表示分配器可以并且很快会释放回OS的页面")
    @JsonAlias(value = "allocator_rss_ratio")
    private String allocatorRssRatio;
    @Schema(description = "当前所有客户端连接中最大的输入缓冲区大小")
    @JsonAlias(value = "client_recent_max_input_buffer")
    private String clientRecentMaxInputBuffer;
    private String clients_in_timeout_table;
    private String aof_last_write_status;
    private String reply_buffer_shrinks;
    @Schema(description = "在编译时选择的内存分配器")
    @JsonAlias(value = "mem_allocator")
    private String memAllocator;
    @Schema(description = "缓存的Lua脚本使用的字节数")
    @JsonAlias(value = "used_memory_scripts")
    private String usedMemoryScripts;
    @Schema(description = "Redis消耗的最大内存峰值（以字节为单位）")
    @JsonAlias(value = "used_memory_peak")
    private String usedMemoryPeak;
    private String number_of_libraries;
    @Schema(description = "Redis消耗的虚拟内存（以字节为单位）")
    @JsonAlias(value = "used_memory_vm_total_human")
    private String usedMemoryVmTotalHuman;
    @Schema(description = "服务器进程的 PID")
    @JsonAlias(value = "process_id")
    private String processId;
    private String master_failover_state;
    @Schema(description = "Redis 服务器消耗的系统 CPU，是进程中所有线程（主线程和后台线程）消耗的系统 CPU 之和")
    @JsonAlias(value = "used_cpu_sys")
    private String usedCpuSys;
    @Schema(description = "复制后备缓冲区的总大小（以字节为单位）")
    @JsonAlias(value = "repl_backlog_size")
    private String replBacklogSize;
    private String connected_slaves;
    private String current_save_keys_total;
    private String reply_buffer_expands;
    private String gcc_version;
    private String total_system_memory_human;
    private String used_memory_functions;
    private String sync_full;
    @Schema(description = "当前客户端连接数（不包括复制品连接）")
    @JsonAlias(value = "connected_clients")
    private String connectedClients;
    private String module_fork_last_cow_size;
    private String total_active_defrag_time;
    @Schema(description = "已处理的写事件总数")
    @JsonAlias(value = "total_writes_processed")
    private String total_writes_processed;
    @Schema(description = "分配器活动页面中的总字节数，包括外部碎片")
    @JsonAlias(value = "allocator_active")
    private String allocatorActive;
    @Schema(description = "写入网络的总字节数")
    @JsonAlias(value = "total_net_output_bytes")
    private String totalNetOutputBytes;
    private String pubsub_channels;
    private String current_fork_perc;
    private String active_defrag_key_hits;
    @Schema(description = "上次dump以来更改的次数")
    @JsonAlias(value = "rdb_changes_since_last_save")
    private String rdbChangesSinceLastSave;
    @Schema(description = "每秒从网络读入的速率，以 KB/sec 为单位")
    @JsonAlias(value = "instantaneous_input_kbps")
    private String instantaneousInputKbps;
    @Schema(description = "从操作系统上显示已经分配的内存总量")
    @JsonAlias(value = "used_memory_rss_human")
    private String usedMemoryRssHuman;
    @Schema(description = "服务器的配置频率设置")
    @JsonAlias(value = "configured_hz")
    private String configuredHz;
    private String expired_stale_perc;
    private String active_defrag_misses;
    @Schema(description = "Redis 后台进程消耗的系统 CPU")
    @JsonAlias(value = "used_cpu_sys_children")
    private String usedCpuSysChildren;
    private String number_of_cached_scripts;
    private String sync_partial_ok;
    @Schema(description = "Lua引擎使用的字节数")
    @JsonAlias(value = "used_memory_lua_human")
    private String usedMemoryLuaHuman;
    @Schema(description = "上次成功的RDB保存的基于Epoch的时间戳")
    @JsonAlias(value = "rdb_last_save_time")
    private String rdbLastSaveTime;
    private String pubsub_patterns;
    private String slave_expires_tracked_keys;
    private String redis_git_sha1;
    @Schema(description = "从操作系统上显示已经分配的内存总量")
    @JsonAlias(value = "used_memory_rss")
    private String usedMemoryRss;
    private String rdb_last_bgsave_time_sec;
    @Schema(description = "操作系统")
    @JsonAlias(value = "os")
    private String os;
    private String mem_not_counted_for_evict;
    private String active_defrag_running;
    private String rejected_connections;
    private String current_eviction_exceeded_time;
    private String total_forks;
    private String active_defrag_key_misses;
    @Schema(description = " 分配器分配的总字节数，包括内部碎片。通常与used_memory相同")
    @JsonAlias(value = "allocator_allocated")
    private String allocatorAllocated;
    @Schema(description = "每秒从网络读出的速率，以 KB/sec 为单位")
    @JsonAlias(value = "instantaneous_output_kbps")
    private String instantaneousOutputKbps;
    private String second_repl_offset;
    @Schema(description = "在上次RDB加载期间加载的键数。Redis 7.0中添加")
    @JsonAlias(value = "rdb_last_load_keys_loaded")
    private String rdbLastLoadKeysLoaded;
    private String rdb_bgsave_in_progress;
    @Schema(description = "Redis 后台进程消耗的用户 CPU")
    @JsonAlias(value = "used_cpu_user_children")
    private String usedCpuUserChildren;
    @Schema(description = "服务器接受的连接总数")
    @JsonAlias(value = "total_connections_received")
    private String totalConnectionsReceived;
    private String migrate_cached_sockets;
}
