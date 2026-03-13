package com.campus.yujianhaowu.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.exception.BusinessException;
import com.campus.yujianhaowu.mapper.CulturalContentMapper;
import com.campus.yujianhaowu.model.entity.CulturalContent;
import com.campus.yujianhaowu.model.entity.User;
import com.campus.yujianhaowu.model.vo.CulturalContentVO;
import com.campus.yujianhaowu.service.CulturalContentService;
import com.campus.yujianhaowu.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文化资讯 Service 实现类
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CulturalContentServiceImpl implements CulturalContentService {

    private final CulturalContentMapper culturalContentMapper;
    private final UserService userService;

    @Override
    public Page<CulturalContentVO> page(Long current, Long size, String category) {
        Page<CulturalContent> page = new Page<>(current, size);
        LambdaQueryWrapper<CulturalContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CulturalContent::getIsPublished, true)
                .eq(StrUtil.isNotBlank(category), CulturalContent::getCategory, category)
                .orderByDesc(CulturalContent::getIsTop)
                .orderByDesc(CulturalContent::getPublishedAt);

        Page<CulturalContent> contentPage = culturalContentMapper.selectPage(page, wrapper);
        Page<CulturalContentVO> voPage = new Page<>(current, size, contentPage.getTotal());
        voPage.setRecords(contentPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());
        return voPage;
    }

    @Override
    public Page<CulturalContentVO> getRecommendContents(Long current, Long size) {
        Page<CulturalContent> page = new Page<>(current, size);
        LambdaQueryWrapper<CulturalContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CulturalContent::getIsPublished, true)
                .eq(CulturalContent::getIsRecommend, true)
                .orderByDesc(CulturalContent::getIsTop)
                .orderByDesc(CulturalContent::getPublishedAt);

        Page<CulturalContent> contentPage = culturalContentMapper.selectPage(page, wrapper);
        Page<CulturalContentVO> voPage = new Page<>(current, size, contentPage.getTotal());
        voPage.setRecords(contentPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());
        return voPage;
    }

    @Override
    public CulturalContentVO getById(Long id) {
        CulturalContent content = culturalContentMapper.selectById(id);
        if (content == null) {
            throw new BusinessException("资讯不存在");
        }
        return convertToVO(content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseViewCount(Long id) {
        CulturalContent content = culturalContentMapper.selectById(id);
        if (content != null) {
            content.setViewCount(content.getViewCount() + 1);
            culturalContentMapper.updateById(content);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initializeData() {
        // 检查是否已有数据
        long count = culturalContentMapper.selectCount(null);
        if (count > 0) {
            log.info("文化资讯表已有 {} 条数据，跳过初始化", count);
            return;
        }

        log.info("开始初始化文化资讯数据...");

        try {
            // 生成非物质文化遗产类内容
            List<CulturalContent> contents = generateAICulturalContents();

            // 保存数据
            for (CulturalContent content : contents) {
                culturalContentMapper.insert(content);
            }

            log.info("成功初始化 {} 条文化资讯数据", contents.size());
        } catch (Exception e) {
            log.error("初始化文化资讯数据失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 生成 AI 文化资讯内容
     */
    private List<CulturalContent> generateAICulturalContents() {
        log.info("开始生成 AI 文化资讯内容...");

        // 模拟 AI 生成的内容（实际项目中应该调用 AI API）
        // 这里使用预设的模板数据来模拟 AI 生成

        return generateCulturalContents();
    }

    /**
     * 生成文化资讯数据（模拟 AI 生成）
     */
    private List<CulturalContent> generateCulturalContents() {
        // 这里生成一些示例数据
        List<CulturalContent> contents = List.of(
                createContent(
                        "豫剧：中原文化的瑰宝",
                        "豫剧是中国五大戏曲剧种之一，发源于河南，流行于全国，以其高亢激昂的唱腔和生动活泼的表演风格深受观众喜爱。",
                        "intangible_heritage",
                        "豫剧，原名河南梆子，是中国梆子剧系的代表剧种之一。它形成于明末清初，距今已有 300 多年的历史。\n\n豫剧的艺术特点\n\n豫剧的唱腔音乐属板腔体，主要板式有慢板、二八板、流水板、飞板等。唱腔高亢激昂、刚劲有力，善于表现慷慨悲壮的情感。\n\n豫剧的表演艺术\n\n豫剧的表演行当齐全，生、旦、净、丑四大行当都有完整的表演程式。表演风格朴实自然，生活气息浓厚，善于刻画人物性格。\n\n经典剧目\n\n豫剧的经典剧目有《朝阳沟》、《花木兰》、《穆桂英挂帅》、《七品芝麻官》等，其中《花木兰》更是家喻户晓，成为豫剧的代表作。\n\n传承与发展\n\n2006 年，豫剧被列入第一批国家级非物质文化遗产名录。如今，豫剧艺术在传承中不断创新，涌现出一大批优秀的青年演员，让这门古老的艺术焕发出新的生机。",
                        "豫剧，戏曲，非物质文化遗产，中原文化",
                        "https://picsum.photos/seed/yuju/800/600"),
                createContent(
                        "少林功夫：禅武合一的东方智慧",
                        "少林功夫是中国武术的代表，融合了佛教禅宗思想，形成了独特的禅武文化，被誉为'天下功夫出少林'。",
                        "intangible_heritage",
                        "少林功夫发源于河南登封嵩山少林寺，距今已有 1500 多年的历史。它是中国武术体系中最具影响力的流派之一。\n\n禅武合一\n\n少林功夫的核心理念是'禅武合一'，即在习武的过程中参悟佛法，在修禅的过程中强健体魄。这种独特的修行方式，使少林功夫超越了单纯的武术范畴，成为一种文化传承。\n\n功夫体系\n\n少林功夫体系完整，内容丰富，包括拳术、器械、对练、气功等多个方面。其中少林七十二绝技更是闻名遐迩，每一绝技都蕴含着深厚的内功修为。\n\n国际影响\n\n如今，少林功夫已经传播到世界各地，成为全球了解中国文化的重要窗口。少林寺在海外建立了多个文化中心，每年吸引数百万外国游客前来参观学习。\n\n传承保护\n\n2006 年，少林功夫被列入国家级非物质文化遗产名录。少林寺通过开办武校、举办国际武术节等方式，积极传承和弘扬少林功夫文化。",
                        "少林功夫，武术，禅宗，非物质文化遗产",
                        "https://picsum.photos/seed/shaolin/800/600"),
                createContent(
                        "唐三彩：盛唐气象的陶瓷艺术",
                        "唐三彩是唐代盛行的一种低温釉陶器，以黄、绿、白三色为主，造型生动，色彩斑斓，展现了盛唐的繁荣气象。",
                        "intangible_heritage",
                        "唐三彩是唐代陶瓷艺术的杰出代表，因以黄、绿、白三种颜色最为常见而得名。它起源于西汉，盛行于唐代，距今已有 1300 多年的历史。\n\n艺术特色\n\n唐三彩的造型丰富多彩，主要有人物、动物、器物三大类。其中人物俑神态各异，动物俑栩栩如生，器物俑精美绝伦，充分展现了唐代工匠的高超技艺。\n\n制作工艺\n\n唐三彩采用二次烧成法，先烧制素胎，然后施釉再烧。釉料中含有铜、铁、钴等金属元素，在烧制过程中产生化学反应，形成绚丽多彩的釉色。\n\n历史价值\n\n唐三彩不仅是精美的艺术品，更是研究唐代社会生活、服饰文化、对外交流的重要实物资料。唐三彩中的人物俑、骆驼俑等，生动记录了唐代的社会风貌。\n\n传承创新\n\n如今，洛阳唐三彩烧制技艺已被列入国家级非物质文化遗产。现代工匠在传承传统技艺的基础上，不断创新，使唐三彩艺术焕发新的活力。",
                        "唐三彩，陶瓷，唐代，非物质文化遗产",
                        "https://picsum.photos/seed/tangsancai/800/600"),
                createContent(
                        "开封清明上河园：穿越千年的大宋风华",
                        "清明上河园是以宋代张择端《清明上河图》为蓝本建造的大型宋代文化主题公园，再现了北宋都城汴京的繁华景象。",
                        "exhibition",
                        "清明上河园位于河南省开封市，是一座以宋代文化为主题的大型历史文化公园。园区按照北宋著名画家张择端的《清明上河图》为蓝本建造，占地 600 余亩。\n\n园区布局\n\n园区分为迎宾广场、北苑和南苑三个部分，再现了北宋时期汴京的城市风貌。园内建筑古朴典雅，小桥流水，亭台楼阁，仿佛让人穿越回了千年前的北宋。\n\n文化演艺\n\n清明上河园每天有数十场文化演艺节目，包括《包公迎宾》、《杨志卖刀》、《王员外招婿》等，演员们身着宋装，生动再现了宋代市井生活。\n\n非遗展示\n\n园区内设有多个非物质文化遗产展示区，游客可以近距离观赏汴绣、官瓷、木版年画等传统工艺的制作过程，感受传统文化的魅力。\n\n夜游体验\n\n清明上河园的夜景更是一绝。大型水上实景演出《大宋·东京梦华》，运用高科技手段，将观众带入梦幻般的北宋盛世，场面宏大，震撼人心。",
                        "清明上河园，开封，宋代文化，主题公园",
                        "https://picsum.photos/seed/qmshy/800/600"),
                createContent(
                        "河南博物院：中原文明的宝库",
                        "河南博物院是国家级重点博物馆，馆藏文物 17 万余件，其中珍贵文物 5 万余件，展现了中原地区五千年的文明史。",
                        "exhibition",
                        "河南博物院创建于 1927 年，位于河南省郑州市农业路，是新中国成立后建成的第一座省级博物馆，也是中央与地方共建的国家级重点博物馆。\n\n建筑特色\n\n河南博物院主体展馆呈金字塔形，顶部为覆斗形，寓意'天圆地方'。建筑外观庄重大气，内部空间宽敞明亮，充分体现了中原文化的厚重底蕴。\n\n馆藏精品\n\n河南博物院的馆藏文物以史前文物、商周青铜器、历代陶瓷器、玉器最具特色。其中贾湖骨笛、杜岭方鼎、莲鹤方壶、云纹铜禁等镇馆之宝，都是国宝级文物。\n\n基本陈列\n\n博物院的基本陈列'泱泱华夏 择中建都'，以中原文明的发展脉络为主线，展示了从史前时期到明清时期的中原文明发展历程。\n\n文化活动\n\n河南博物院常年举办各类专题展览和文化讲座，推出的'华夏古乐'音乐文物复原演出，让千年古乐重现人间，深受观众喜爱。",
                        "河南博物院，博物馆，中原文化，文物",
                        "https://picsum.photos/seed/henanmar/800/600"),
                createContent(
                        "洛阳牡丹文化节：国色天香的盛宴",
                        "洛阳牡丹文化节每年 4 月举办，届时百万株牡丹竞相开放，吸引数百万游客前来赏花，成为洛阳的城市名片。",
                        "activity",
                        "洛阳牡丹甲天下，花开时节动京城。洛阳牡丹文化节始于 1983 年，至今已成功举办了 40 多届，是全国知名的文化旅游节庆品牌。\n\n牡丹品种\n\n洛阳牡丹栽培历史悠久，始于隋朝，盛于唐朝。现有牡丹品种 1200 多个，花色有红、粉、黄、白、紫、蓝、绿、黑、复色九大色系，花型有单瓣、半重瓣、重瓣等多种类型。\n\n主要园区\n\n洛阳牡丹文化节的举办地点主要有王城公园、中国国花园、隋唐城遗址植物园、国家牡丹园等多个园区。每个园区都有各自的特色，游客可以根据喜好选择游览。\n\n文化活动\n\n牡丹文化节期间，除了赏花，还有牡丹书画展、牡丹摄影展、牡丹邮票展、牡丹音乐会等丰富多彩的文化活动，让游客在赏花的同时，感受牡丹文化的魅力。\n\n旅游服务\n\n文化节期间，洛阳市会开通赏花专线公交车，延长景区开放时间，推出联票优惠等措施，为游客提供便捷优质的服务。",
                        "洛阳牡丹，文化节，赏花，旅游",
                        "https://picsum.photos/seed/mudan/800/600"),
                createContent(
                        "太极拳：刚柔并济的养生之道",
                        "太极拳发源于河南温县陈家沟，是中国传统武术的瑰宝，融合了阴阳五行思想，兼具健身、养生、技击功能。",
                        "intangible_heritage",
                        "太极拳是中国武术的杰出代表，发源于河南省焦作市温县陈家沟，由明末清初的陈王廷所创，距今已有 400 多年的历史。\n\n文化内涵\n\n太极拳以太极阴阳学说为核心，融合了儒家、道家、医家的思想精髓。拳理中蕴含着阴阳变化、刚柔相济、以柔克刚的哲学思想，体现了中华传统文化的深厚底蕴。\n\n主要流派\n\n太极拳发展至今，形成了陈式、杨式、吴式、武式、孙式五大流派。其中陈式太极拳是各流派的源头，保留了较多的技击特色；杨式太极拳则更注重养生健身，传播最广。\n\n健身价值\n\n太极拳动作柔和缓慢，适合各个年龄段的人群练习。长期练习可以增强体质、调节身心、延年益寿，被联合国教科文组织列为人类非物质文化遗产代表作。\n\n国际传播\n\n如今，太极拳已传播到 150 多个国家和地区，全球练习者超过 3 亿人。每年都有大量外国友人来到陈家沟学习太极拳，感受中国传统文化的魅力。",
                        "太极拳，武术，养生，非物质文化遗产",
                        "https://picsum.photos/seed/taiji/800/600"),
                createContent(
                        "2026 年河南文化旅游活动日历",
                        "2026 年河南各地将举办丰富多彩的文化旅游活动，包括传统节日庆典、非遗展示、文化演出等，为游客带来精彩纷呈的文化体验。",
                        "activity",
                        "2026 年，河南省各地将围绕传统文化节日和特色文化资源，举办一系列精彩纷呈的文化旅游活动。\n\n春季活动（3-5 月）\n\n3 月：开封清明文化节\n4 月：洛阳牡丹文化节、新郑黄帝故里拜祖大典\n5 月：安阳殷商文化旅游节\n\n夏季活动（6-8 月）\n\n6 月：郑州国际少林武术节\n7 月：云台山避暑旅游季\n8 月：信阳茶文化节\n\n秋季活动（9-11 月）\n\n9 月：洛阳河洛文化旅游节\n10 月：开封菊花文化节、商丘国际华商节\n11 月：焦作云台山红叶节\n\n冬季活动（12-2 月）\n\n12 月：安阳中国文字节\n1 月：各地春节庙会\n2 月：浚县古庙会\n\n特色活动\n\n全年期间，清明上河园、开封府、大宋武侠城等景区将每天推出特色文化演艺活动，让游客沉浸式体验宋代文化。\n\n温馨提示\n\n建议游客提前关注各活动官方信息，合理安排行程。部分热门活动可能需要提前预约或购票。",
                        "文化旅游，活动日历，节庆，河南旅游",
                        "https://picsum.photos/seed/calendar/800/600"));

        log.info("生成了 {} 条文化资讯内容", contents.size());
        return contents;
    }

    /**
     * 创建文化资讯内容
     */
    private CulturalContent createContent(String title, String summary, String category,
            String content, String tags, String coverImageUrl) {
        CulturalContent contentEntity = new CulturalContent();
        contentEntity.setTitle(title);
        contentEntity.setSummary(summary);
        contentEntity.setCategory(category);
        contentEntity.setContent(content);
        contentEntity.setTags(JSONUtil.toJsonStr(tags.split(",")));
        contentEntity.setCoverImage(coverImageUrl);
        contentEntity.setIsPublished(true);
        contentEntity.setIsRecommend(true);
        contentEntity.setIsTop(false);
        contentEntity.setViewCount(0);
        contentEntity.setLikeCount(0);
        contentEntity.setCommentCount(0);
        contentEntity.setFavoriteCount(0);
        contentEntity.setPublishedAt(java.time.LocalDateTime.now());

        return contentEntity;
    }

    /**
     * 转换为 VO
     */
    private CulturalContentVO convertToVO(CulturalContent content) {
        CulturalContentVO vo = new CulturalContentVO();
        BeanUtils.copyProperties(content, vo);

        // 获取作者信息
        if (content.getAuthorId() != null) {
            User author = userService.getById(content.getAuthorId());
            if (author != null) {
                vo.setAuthorName(author.getNickname());
            }
        }

        return vo;
    }
}
