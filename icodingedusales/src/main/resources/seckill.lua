--
-- 秒杀业务使用的lua脚本
-- To change this template use File | Settings | File Templates.
--

-- 1.判断库存 2。判断用户是否购买过 3.减库存，记录用户的购买
local product_id = KEYS[1]
local user_id = ARGV[1]
-- lua使用 .. 表示字符串拼接
local bought_users_key = 'seckill:uids:' .. product_id
local product_key = 'seckill:stock:' .. product_id
--脚本中间的内容通过日志看到
redis.log(1,product_id)
redis.log(1,user_id)
redis.log(1,bought_users_key)
redis.log(1,product_key)
--判断用户是否购买过
--调函数出错时报 @user_script: 18: Wrong number of args calling Redis command From Lua script，之前少传一个user_id
local has_bought = redis.call('sismember',bought_users_key,user_id)
--has_bought大于0表示购买过
if has_bought > 0 then
    return 0;
end
--判断商品库存是否足够
local stock = redis.call('get',product_key)
--not stock表示product_id不存在
if not stock or tonumber(stock) <= 0 then
    return -1
end
--减库存并记录购买用户id
redis.call('decr',product_key)
redis.call('sadd',bought_users_key,user_id)
return 1