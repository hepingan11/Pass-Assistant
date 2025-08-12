package top.hepingan.pacommon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.hepingan.pacommon.entity.Drawing;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 雨纷纷旧故里草木深
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Mapper
public interface DrawingMapper extends BaseMapper<Drawing> {
    @Update("update drawing set is_public = 1 where generate_url = #{url}")
    void setPublic(String url);

    @Delete("delete from drawing where generate_url = #{url}")
    void deleteDraw(String url);
}
