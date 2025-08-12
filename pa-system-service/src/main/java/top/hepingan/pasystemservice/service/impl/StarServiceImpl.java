package top.hepingan.pasystemservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.hepingan.pacommon.dto.StarDialogueDto;
import top.hepingan.pacommon.entity.Star;
import top.hepingan.pacommon.mapper.StarMapper;
import top.hepingan.pacommon.utils.BeanUtils;
import top.hepingan.pacommon.utils.UserUtils;
import top.hepingan.pacommon.vo.UserStarDetailVo;
import top.hepingan.pacommon.vo.UserStarVo;
import top.hepingan.pasystemservice.service.StarService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StarServiceImpl implements StarService {
    private final StarMapper starMapper;

    @Override
    public long starDialogue(final StarDialogueDto dto) {
        final Star star = new Star()
                .setUserId(UserUtils.getCurrentLoginId())
                .setIssue(dto.getIssue())
                .setAnswer(dto.getAnswer());
        starMapper.insert(star);

        return star.getStarId();
    }

    @Override
    public IPage<UserStarVo> getUserStarVoPage(final int pageNum) {
        final Long currentLoginId = UserUtils.getCurrentLoginId();
        return starMapper.selectPage(new Page<>(pageNum, 15), new QueryWrapper<Star>()
                .lambda()
                .eq(Star::getUserId, currentLoginId)
                .select(Star::getIssue, Star::getCreatedTime, Star::getStarId)
        ).convert(c -> new UserStarVo().setIssue(c.getIssue()).setStarId(c.getStarId()).setCreatedTime(c.getCreatedTime()));
    }

    @Override
    public UserStarDetailVo getUserStarDetail(final Long id) {

        final Star star = starMapper.selectOne(new QueryWrapper<Star>()
                .lambda().eq(Star::getStarId, id)
                .eq(Star::getUserId, UserUtils.getCurrentLoginId())
                .select(Star::getAnswer, Star::getIssue, Star::getStarId, Star::getCreatedTime)
        );
        if (star == null) {
            return null;
        }
        return BeanUtils.copyClassProperTies(star, UserStarDetailVo.class);

    }

    @Override
    public void deleteById(final Long id) {
        starMapper.delete(new QueryWrapper<Star>()
                .lambda()
                .eq(Star::getStarId, id)
                .eq(Star::getUserId, UserUtils.getCurrentLoginId())
        );
    }


    @Override
    public List<UserStarVo> getUserStarWeb() {
        return starMapper.selectList(new QueryWrapper<Star>()
                .lambda().eq(Star::getUserId, UserUtils.getCurrentLoginId())
                .select(Star::getAnswer, Star::getIssue, Star::getCreatedTime, Star::getStarId)
                .orderByDesc(Star::getCreatedTime)
        ).stream().map(s -> new UserStarVo().setStarId(s.getStarId()).setAnswer(s.getAnswer()).setIssue(s.getIssue()).setCreatedTime(s.getCreatedTime())).toList();
    }
}
