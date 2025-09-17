create table code
(
    code_id      bigint auto_increment
        primary key,
    code_name    varchar(120)  not null comment '项目名称',
    user_id      bigint        null,
    look         int           null comment '点击人数',
    introduce    varchar(3000) null comment '介绍',
    is_public    int           not null comment '是否审核通过(1/0)',
    price        double        not null comment '价格',
    download_url varchar(250)  null comment '下载地址',
    language     varchar(50)   null comment '语言',
    created_time datetime      not null,
    update_time  datetime      not null
)
    comment '代码项目';

create table code_exchange
(
    code_exchange_id bigint auto_increment
        primary key,
    order_no         varchar(100) null,
    sell_user_id     bigint       not null comment '售卖人id',
    buy_user_id      bigint       not null comment '购买人id',
    code_id          bigint       not null comment '交易代码id',
    price            double       not null comment '交易金额',
    status           varchar(50)  null comment '状态',
    created_time     datetime     null
)
    comment '代码交易';

create table code_image
(
    code_image_id bigint auto_increment
        primary key,
    code_id       bigint       not null,
    image_url     varchar(200) not null comment '图'
)
    comment '代码项目图片';

create table conversation_user
(
    conversation_id varchar(36)  not null comment '对话id',
    user_id         bigint       not null comment '用户id',
    title           varchar(255) null comment '对话名称',
    created_time    datetime     not null comment '创建时间',
    role            varchar(350) null comment '系统角色'
)
    comment '用户对话索引';

create table drawing
(
    drawing_id   bigint auto_increment comment '绘图ID'
        primary key,
    user_id      bigint                             not null comment '所属用户',
    prompt       text                               not null comment '提示词',
    generate_url varchar(700)                       null comment '生成图',
    is_public    tinyint  default 0                 not null comment '是否公开',
    model        varchar(30)                        null comment '使用的模型',
    size         varchar(15)                        null comment '大小',
    image        varchar(200)                       null comment '参考图片',
    created_time datetime default CURRENT_TIMESTAMP not null comment '创建时间'
);

create index drawing_generate_url_index
    on drawing (generate_url);

create index idx_created_time
    on drawing (created_time);

create index idx_is_public
    on drawing (is_public);

create index idx_user_id
    on drawing (user_id);

create table exchange
(
    exchange_id   bigint auto_increment comment '兑换码ID'
        primary key,
    exchange_code varchar(8)                         not null comment '兑换码',
    frequency     bigint                             not null comment '兑换码所含Ai币',
    created_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null comment '修改时间',
    constraint exchange_code
        unique (exchange_code)
);

create index idx_created_time
    on exchange (created_time);

create index idx_exchange_code
    on exchange (exchange_code);

create index idx_frequency
    on exchange (frequency);

create index idx_update_time
    on exchange (update_time);

create table link
(
    link_id      bigint auto_increment comment 'id'
        primary key,
    user_id      bigint                             not null comment '申请人用户id',
    link_name    varchar(50)                        not null comment '链接名称',
    link_url     varchar(255)                       not null comment '链接地址',
    link_intro   varchar(200)                       not null comment '链接简介',
    link_sort    varchar(20)                        not null comment '链接分类',
    link_img     varchar(200)                       not null comment '链接封面',
    is_public    tinyint  default 0                 not null comment '是否申请成功',
    is_hot       tinyint  default 0                 not null comment '是否为热门',
    created_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null comment '更新时间'
)
    comment '链接';

create index idx_created_time
    on link (created_time);

create index idx_is_hot
    on link (is_hot);

create index idx_is_public
    on link (is_public);

create index idx_update_time
    on link (update_time);

create index link_user_id
    on link (user_id);

create table link_stat
(
    link_stat_id bigint auto_increment comment 'id'
        primary key,
    user_id      bigint not null comment '用户id',
    stat_id      bigint not null
)
    comment '用户收藏链接';

create index stat_link_stat_id
    on link_stat (stat_id);

create index stat_link_user_id
    on link_stat (user_id);

create table mcps
(
    mcps_id      bigint auto_increment
        primary key,
    method_name  varchar(50)  not null comment '方法名(必须与调用方法名一样)',
    mcp_name     varchar(100) not null comment 'mcp名称',
    introduce    varchar(400) null comment '介绍',
    icon         varchar(200) null comment '图标url',
    created_time datetime     null
)
    comment 'mcp服务列表';

create table orders
(
    orders_id      varchar(100)                       not null
        primary key,
    user_id        bigint                             not null,
    product_id     bigint                             not null,
    product_name   varchar(50)                        not null,
    product_price  double                             not null,
    state          tinyint                            not null,
    created_time   datetime default CURRENT_TIMESTAMP not null,
    update_time    datetime default CURRENT_TIMESTAMP not null,
    pay_time       datetime                           null,
    frequency      bigint                             null,
    reason_failure varchar(50)                        null
);

create index orders_product_id_index
    on orders (product_id);

create index orders_state_index
    on orders (state);

create index orders_user_id_index
    on orders (user_id);

create table personality
(
    personality_id bigint auto_increment comment '主键ID'
        primary key,
    user_id        bigint                             not null comment '所属用户',
    model          varchar(50)                        not null comment '模型名称',
    top_p          tinyint                            not null,
    max_tokens     bigint                             not null comment '上传图',
    temperature    tinyint                            not null comment '生成图',
    open_key       varchar(255)                       not null comment 'chatgpt密钥',
    open_ai_url    varchar(255)                       not null comment 'chatgpt请求地址',
    questions      text                               not null comment '问题',
    answer         text                               not null comment '回答',
    speed          bigint                             not null comment '回复速率',
    created_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null comment '修改时间'
);

create index personality_user_id_index
    on personality (user_id);

create table photo
(
    photo_id     bigint auto_increment
        primary key,
    url          varchar(200) not null comment 'url',
    name         varchar(100) null comment 'name',
    user_id      bigint       not null comment '用户id',
    created_time datetime     not null,
    is_public    tinyint(1)   not null
);

create index photo_user_id
    on photo (user_id);

create table product
(
    product_id    bigint auto_increment
        primary key,
    product_name  varchar(100)                       not null,
    frequency     bigint                             not null,
    product_price double                             not null,
    created_time  datetime default CURRENT_TIMESTAMP not null,
    update_time   datetime default CURRENT_TIMESTAMP not null
);

create index product_product_name_index
    on product (product_name);

create table rag
(
    rag_id       bigint auto_increment
        primary key,
    rag_url      varchar(200) not null comment 'rag知识库地址',
    rag_name     varchar(100) null,
    user_id      bigint       not null,
    is_enable    tinyint      not null comment '是否启用',
    created_time datetime     not null comment '创建时间'
)
    comment '用户知识库';

create table sd_model
(
    sd_model_id  bigint auto_increment
        primary key,
    model_name   varchar(100)                       not null,
    text_name    varchar(200)                       not null,
    created_time datetime default CURRENT_TIMESTAMP not null,
    update_time  datetime default CURRENT_TIMESTAMP not null
);

create index sd_model_model_name_text_name_index
    on sd_model (model_name, text_name);

create table spring_ai_chat_memory
(
    id              bigint auto_increment
        primary key,
    conversation_id varchar(36)  not null,
    content         text         not null,
    media           varchar(160) null comment '媒体',
    type            varchar(10)  not null,
    timestamp       timestamp    not null,
    model           varchar(36)  null comment '模型名',
    is_mcp          tinyint(1)   null comment '是否开启了mcp',
    is_rag          tinyint      null comment '是否开启了rag'
);

create index SPRING_AI_CHAT_MEMORY_CONVERSATION_ID_TIMESTAMP_IDX
    on spring_ai_chat_memory (conversation_id, timestamp);

create table star
(
    star_id      bigint auto_increment comment '主键'
        primary key,
    user_id      bigint                             not null comment '所属用户',
    issue        longtext                           not null comment '问题',
    answer       longtext                           not null comment '答案',
    created_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null comment '修改时间'
);

create index idx_created_time
    on star (created_time);

create index idx_update_time
    on star (update_time);

create index idx_user_id
    on star (user_id);

create table user
(
    user_id      bigint auto_increment comment '主键'
        primary key,
    open_id      varchar(180)                                     null comment '微信用户标识',
    avatar       varchar(200)                                     null comment '用户头像',
    user_name    varchar(100)                                     null comment '用户微信昵称',
    email        varchar(200)                                     null,
    password     varchar(255)                                     null,
    type         enum ('ADMIN', 'USER') default 'USER'            not null,
    frequency    bigint                 default 0                 not null comment 'Ai币',
    is_sign_in   tinyint                default 0                 not null comment '是否签到',
    created_time datetime               default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime               default CURRENT_TIMESTAMP not null comment '修改时间'
);

create index idx_created_time
    on user (created_time);

create index idx_frequency
    on user (frequency);

create index idx_open_id
    on user (open_id);

create index idx_update_time
    on user (update_time);

create index user_email_password_index
    on user (email, password);

create table video
(
    video_id     bigint auto_increment
        primary key,
    prompt       varchar(500)         not null comment '提示词',
    status       varchar(10)          not null comment '状态',
    user_id      bigint               not null,
    img_url      varchar(200)         null comment '图片url',
    task_id      varchar(100)         not null comment '任务ID',
    video_url    varchar(500)         null comment '视频url地址',
    cover_url    varchar(200)         null comment '视频封面图',
    created_time datetime             not null comment '创建时间',
    is_public    tinyint(1) default 0 null
)
    comment '视频生成';

create table video_detail
(
    video_detail_id   bigint auto_increment
        primary key,
    video_id          bigint       not null,
    model             varchar(100) null comment '模型',
    seed              int          null comment '本次请求使用的种子整数值',
    resolution        varchar(10)  null comment '生成视频的分辨率',
    ratio             varchar(10)  null comment '视频的宽高比',
    duration          int          null comment '视频的时长，单位：秒',
    framespersecond   int          null comment '生成视频的帧率',
    completion_tokens int          not null comment '模型生成的 token 数量'
)
    comment '视频详细';

create table work
(
    work_id      bigint auto_increment comment 'id'
        primary key,
    name         varchar(100) not null comment '名字',
    url          varchar(200) not null comment 'url',
    sort         varchar(100) null comment '类型',
    created_time datetime     not null,
    updated_time datetime     not null
);

