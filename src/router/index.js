import {createRouter, createWebHashHistory} from 'vue-router'
import {cancelArr} from "@/utils/BSideRequest";

const routes = [{
    path: '/app',
    name: 'Index',
    component: () => import('../views/DialogueView.vue'),
    meta: {
        title: '智能问答', // TODO 浏览器标题
        isHeadNavigation: true, // TODO 是否显示tab
        keepAlive: true,
        isLeftMenu: true
    }
    },
    {
        path: '/create',
        name: 'Create',
        component: () => import('../views/Create/CreateView.vue'),
        meta: {
            title: '灵感创作',
            isHeadNavigation: true,
            keepAlive: false,
            isLeftMenu: true
        }
    },
    {
        path: '/',
        name: 'PassView',
        component: () => import('../views/PassView.vue'),
        meta: {
            title: '灵感创作',
            isHeadNavigation: true,
            keepAlive: false,
            isLeftMenu: false
        }
    },
    {
        path: "/personality_view",
        name: "PersonalityView",
        component: () => import('../views/PersonalityView.vue'),
        meta: {
            title: 'PERSONALITY GPT',
            isHeadNavigation: true,
            keepAlive: true,
            isLeftMenu: true
        }
    },
    {
        path: "/drawing_view",
        name: "NEW DRAWING",
        component: () => import('../views/Drawing/DrawingView.vue'),
        meta: {
            title: '新绘图',
            isHeadNavigation: true,
            keepAlive: true,
            isLeftMenu: false
        },
    },
    {
        path: "/create_edit",
        name: "CreateEdit",
        component: () => import('../views/Create/CreateEditView.vue'),
        meta: {
            title: '创作编辑',
            isHeadNavigation: true,
            keepAlive: false,
            isLeftMenu: true
        },
    },
    {
        path: "/create_detail",
        name: "CreateDetail",
        component: () => import('../views/Create/CreateDetailView.vue'),
        meta: {
            title: '创作结果',
            isHeadNavigation: true,
            keepAlive: false,
            isLeftMenu: true
        },
    },
    {
        path: '/purchase',
        name: 'Purchase',
        component: () => import('../views/PurchaseView.vue'),
        meta: {
            title: '购买',
            isHeadNavigation: true,
            keepAlive: false,
            isLeftMenu: true
        }
    },
    {
        path: '/orders',
        name: 'Orders',
        component: () => import('../views/OrdersView.vue'),
        meta: {
            title: '打赏记录',
            isHeadNavigation: true,
            keepAlive: false,
            isLeftMenu: true
        }
    },
    {
        path: '/collection',
        name: 'Collection',
        component: () => import('../views/CollectionView.vue'),
        meta: {
            title: '我的收藏',
            isHeadNavigation: true,
            keepAlive: false,
            isLeftMenu: true
        }
    },
    {
        path: "/exchange",
        name: "Exchange",
        component: () => import('../views/ExchangeView.vue'),
        meta: {
            title: '兑换中心',
            isHeadNavigation: true,
            keepAlive: false,
            isLeftMenu: true
        }
    },
    {
        path: "/custom",
        name: "Custom",
        component: () => import('../views/CustomView.vue'),
        meta: {
            title: '自定义对话',
            isHeadNavigation: true,
            keepAlive: false,
            isLeftMenu: true
        }
    },
    {
        path: "/digital_view",
        name: "DigitalView",
        component: () => import('../views/DigitalView.vue'),
        meta: {
            title: '数字人平台',
            isHeadNavigation: true,
            keepAlive: true,
            isLeftMenu: true
        }
    },

    {
        path: "/preset_character",
        name: "PresetCharacter",
        component: () => import('../views/PresetCharacterView.vue'),
        meta: {
            title: '预设角色',
            isHeadNavigation: true,
            keepAlive: false,
            isLeftMenu: true
        }
    },
    {
        path: "/laboratory",
        name: "Laboratory",
        component: () => import('../views/LaboratoryView.vue'),
        meta: {
            title: '超级实验室',
            isHeadNavigation: true,
            keepAlive: false,
            isLeftMenu: true
        }
    }
    , {
        path: '/admin',
        name: 'Admin',
        component: () => import('@/views/Admin/AdminView.vue'),
        meta: {
            title: '管理控制台',
            isHeadNavigation: true,
            keepAlive: true,
            isLeftMenu: true
        }
    },
    {
        path: '/link_view',
        name: 'LinkView',
        component: () => import('@/views/LinkView.vue'),
        meta: {
            title: '链接',
            isHeadNavigation: true,
            keepAlive: false,
            isLeftMenu: false
        }
    },
    {
        path: '/apply_view',
        name: 'ApplyLinkView',
        component: () => import('@/views/ApplyLinkView.vue'),
        meta: {
            title: '管理控制台',
            isHeadNavigation: true,
            keepAlive: true,
            isLeftMenu: false
        }
    },
    {
        path: '/user_view',
        name: 'UserView',
        component: () => import('@/views/UserView.vue'),
        meta: {
            title: '用户中心',
            isHeadNavigation: true,
            keepAlive: true,
            isLeftMenu: true
        }
    },
    {
        path: '/data_view',
        name: 'DataView',
        component: () => import('@/views/DataView.vue'),
        meta: {
            title: '数据分析室',
            isHeadNavigation: true,
            keepAlive: true,
            isLeftMenu: true
        }
    },
    {
        path: '/aigc_view',
        name: 'AigcView',
        component: () => import('@/views/AigcView.vue'),
        meta: {
            title: 'AIGC新闻',
            isHeadNavigation: true,
            keepAlive: true,
            isLeftMenu: false
        }
    },
    {
        path: '/passnews_view',
        name: 'PassnewsView',
        component: () => import('@/views/PassnewsView.vue'),
        meta: {
            title: '派斯新闻',
            isHeadNavigation: true,
            keepAlive: true,
            isLeftMenu: false
        }
    },
    {
        path: '/data_test_view',
        name: 'DataTestView',
        component: () => import('@/views/Data/DataTestView.vue'),
        meta: {
            title: '京东数据',
            isHeadNavigation: true,
            keepAlive: true,
            isLeftMenu: false
        }
    },
    {
        path: '/data_flourish_view',
        name: 'DataFlourishView',
        component: () => import('@/views/Data/DataFlourishView.vue'),
        meta: {
            title: 'Flourish数据',
            isHeadNavigation: true,
            keepAlive: true,
            isLeftMenu: false
        }
    },
    {
        path: '/photo_view',
        name: 'PhotoView',
        component: () => import('@/views/PhotoView.vue'),
        meta: {
            title: '图床',
            isHeadNavigation: true,
            keepAlive: true,
            isLeftMenu: true
        }
    },
    {
        path: '/funny',
        name: 'FunnyView',
        component: () => import('@/views/FunnyView.vue'),
        meta: {
            title: '视觉盛宴',
            isHeadNavigation: true,
            keepAlive: false,
            isLeftMenu: false
        }
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})


// TODO 全局前置守卫
router.beforeEach(async (to) => {
    // TODO 页面切换中断所有请求
    cancelArr.forEach((cancel, index) => {
        cancel()
        cancelArr.splice(index, 1)
    })

    // TODO 设置浏览器Title
    document.title = (to.meta.title ? to.meta.title : '') + ' - Pass Assistant'
})

export default router
