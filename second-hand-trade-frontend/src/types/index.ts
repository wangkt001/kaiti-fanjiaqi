// 用户信息
export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar?: string
  phone?: string
  email?: string
  gender?: number
  role: 'buyer' | 'seller' | 'admin'
  sellerStatus?: 'pending' | 'approved' | 'rejected'
  shopName?: string
  shopLogo?: string
  fansCount?: number
  followCount?: number
}

// 商品信息
export interface Goods {
  id: number
  name: string
  description: string
  price: number
  originalPrice?: number
  stock: number
  categoryId: number
  categoryName?: string
  sellerId: number
  sellerName?: string
  shopName?: string
  status: 'pending' | 'on_sale' | 'sold_out' | 'offline'
  salesCount: number
  viewCount: number
  favoriteCount: number
  hasSku: boolean
  giftPackaging: boolean
  customization: boolean
  packagingFee?: number
  images?: GoodsImage[]
  tags?: CulturalTag[]
  culturalInfo?: CulturalInfo
  createdAt: string
  publishedAt?: string
}

// 商品图片
export interface GoodsImage {
  id: number
  productId: number
  imageUrl: string
  type: 'main' | 'detail'
  sortOrder: number
}

// 文化标签
export interface CulturalTag {
  id: number
  name: string
  description?: string
  category?: string
  iconUrl?: string
  sortOrder: number
  isHot: boolean
  isActive: boolean
}

// 商品文化信息
export interface CulturalInfo {
  id: number
  productId: number
  culturalBackground?: string
  craftDescription?: string
  designer?: string
  originPlace?: string
  material?: string
  culturalIp?: string
  storyContent?: string
}

// 分类信息
export interface Category {
  id: number
  name: string
  parentId?: number
  description?: string
  iconUrl?: string
  sortOrder: number
  level: number
  isActive: boolean
  children?: Category[]
}

// 订单信息
export interface Order {
  id: number
  orderNumber: string
  buyerId: number
  sellerId: number
  totalAmount: number
  freightAmount?: number
  packagingFee?: number
  discountAmount?: number
  actualAmount: number
  status: 'pending' | 'paid' | 'shipped' | 'received' | 'completed' | 'cancelled'
  paymentMethod?: 'alipay' | 'wechat' | 'bank'
  paymentTime?: string
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  giftMessage?: string
  logisticsCompany?: string
  logisticsNumber?: string
  shippingTime?: string
  receivedTime?: string
  completedTime?: string
  items: OrderItem[]
  createdAt: string
}

// 订单商品
export interface OrderItem {
  id: number
  orderId: number
  productId: number
  productName: string
  productImage?: string
  skuId?: number
  skuName?: string
  price: number
  quantity: number
  totalAmount: number
}

// 评价信息
export interface Review {
  id: number
  productId: number
  orderId: number
  userId: number
  userName?: string
  userAvatar?: string
  rating: number
  content?: string
  images?: string[]
  isAnonymous: boolean
  likeCount: number
  replyCount: number
  status: number
  createdAt: string
  replies?: ReviewReply[]
}

// 评价回复
export interface ReviewReply {
  id: number
  reviewId: number
  userId: number
  userName?: string
  userAvatar?: string
  content: string
  likeCount: number
  createdAt: string
}

// 文化资讯
export interface CulturalContent {
  id: number
  title: string
  summary?: string
  content: string
  coverImage?: string
  category?: string
  tags?: string[]
  authorId?: number
  authorName?: string
  viewCount: number
  likeCount: number
  commentCount: number
  favoriteCount: number
  isTop: boolean
  isRecommend: boolean
  isPublished: boolean
  publishedAt?: string
  createdAt: string
}

// 购物车项
export interface CartItem {
  id: number
  productId: number
  productName: string
  productImage?: string
  price: number
  quantity: number
  skuId?: number
  skuName?: string
  sellerId: number
  shopName?: string
  stock: number
  selected: boolean
}

// 通用响应
export interface ApiResponse<T = any> {
  total: number
  records: Goods[] | { id: number; name: string; description: string; price: number; originalPrice?: number | undefined; stock: number; categoryId: number; categoryName?: string | undefined; sellerId: number; sellerName?: string | undefined; shopName?: string | undefined; status: "pending" | "on_sale" | "sold_out" | "offline"; salesCount: number; viewCount: number; favoriteCount: number; hasSku: boolean; giftPackaging: boolean; customization: boolean; packagingFee?: number | undefined; images?: { id: number; productId: number; imageUrl: string; type: "main" | "detail"; sortOrder: number }[] | undefined; tags?: { id: number; name: string; description?: string | undefined; category?: string | undefined; iconUrl?: string | undefined; sortOrder: number; isHot: boolean; isActive: boolean }[] | undefined; culturalInfo?: { id: number; productId: number; culturalBackground?: string | undefined; craftDescription?: string | undefined; designer?: string | undefined; originPlace?: string | undefined; material?: string | undefined; culturalIp?: string | undefined; storyContent?: string | undefined } | undefined; createdAt: string; publishedAt?: string | undefined }[]
  code: number
  message: string
  data: T
  timestamp: number
}

// 分页参数
export interface PageParams {
  current: number
  size: number
  [key: string]: any
}

// 分页结果
export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
  pages: number
}

// 登录参数
export interface LoginParams {
  username: string
  password: string
}

// 注册参数
export interface RegisterParams {
  username: string
  password: string
  phone?: string
  email?: string
  nickname?: string
}
