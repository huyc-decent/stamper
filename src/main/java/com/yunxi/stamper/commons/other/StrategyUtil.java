package com.yunxi.stamper.commons.other;

import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.StrategyPassword;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class StrategyUtil {
    //密码策略检查
    /**
     * 检查密码规则
     */
    public static void checkStrategy(String password,StrategyPassword sp){
        if(sp==null){
            return;
        }
        if(StringUtils.isBlank(password)){
            throw new PrintException("密码不能为空");
        }
        Integer first_char = sp.getFirstChar();
        //校验密码长度
        Integer lenMin = sp.getLenMin();
        Integer lenMax = sp.getLenMax();

        if(lenMin!=null && password.length()<lenMin){
            throw new PrintException("密码长度不能少于"+lenMin+"位");
        }
        if(lenMax!=null && password.length()>lenMax){
            throw new PrintException("密码长度不能大于"+lenMax+"位");
        }

        //校验首字母
        if(first_char!=null){
            checkFirst(password,first_char);
        }

        Integer upper_status = sp.getUpperStatus();
        Integer lower_status = sp.getLowerStatus();
        Integer num_status = sp.getNumStatus();
        Integer char_status = sp.getCharStatus();

        if(upper_status!=null && upper_status==1){
            //校验密码是否存在大写字母
            boolean isMatche = password.matches(".*[A-Z]+.*");
            if(!isMatche){
                throw new PrintException("密码中必须存在大写字母");
            }
        }
        if(lower_status!=null&&lower_status==1){
            //校验密码是否存在小写字母
            boolean isMatche = password.matches(".*[a-z]+.*");
            if(!isMatche){
                throw new PrintException("密码中必须存在小写字母");
            }
        }
        if(num_status!=null&&num_status==1){
            //校验密码是否存在数字
            boolean isMatche = password.matches(".*[0-9]+.*");
            if(!isMatche){
                throw new PrintException("密码中必须存在数字");
            }
        }
        if(char_status!=null&&char_status==1){
            //校验密码是否存在非单词字符
            boolean isMatche = password.matches(".*\\W+.*");
            if(!isMatche){
                throw new PrintException("密码中必须存在特殊字符");
            }
        }
    }

    //校验首字母规则 0:数字 1:大写英文 2:小写英文 3:特殊字符
    private static void checkFirst(String password, int filter) {
        char firstChar = password.toCharArray()[0];
        switch (filter){
            case 0:
                if(!(firstChar>='0' && firstChar<='9')){
                    throw new PrintException("密码首字母必须为数字");
                }
                break;
            case 1:
                if(!(firstChar>='A' && firstChar<='Z')){
                    throw new PrintException("首字母必须为大写字母");
                }
                break;
            case 2:
                if(!(firstChar>='a' && firstChar<='z')){
                    throw new PrintException("首字母必须为小写字母");
                }
                break;
            case 3:
                if((firstChar>='0' && firstChar<='9') || (firstChar>='A' && firstChar<='Z') || (firstChar>='a' && firstChar<='z')){
                    throw new PrintException("首字母必须为非字母数字的特殊字符");
                }
                break;
            default:
        }
    }

}
