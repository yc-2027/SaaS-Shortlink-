-- KEYS[1] = Bloom-Filter key           (shortUriCreateCachePenetrationBloomFilter)
-- KEYS[2] = 正向缓存 key               (GOTO_SHORT_LINK:{url})
-- KEYS[3] = 空值缓存 key               (GOTO_IS_NULL_SHORT_LINK:{url})
-- ARGV[1] = 要检测的完整短链 fullShortUrl
-- 返回值:
--   {1, val}  => 命中正向缓存
--   {2}       => 命中空值缓存（404）
--   {3}       => Bloom 里不存在（404）
--   {4}       => Bloom 存在但缓存未命中，需查询 DB 并回写

-- 1) 先查正向缓存
local v = redis.call('GET', KEYS[2])
if v then
  return {1}
end

-- 2) 查空值缓存（“-”）
local n = redis.call('GET', KEYS[3])
if n then
  return {2}
end

-- 3) Bloom-Filter 判断
local exist = redis.call('BF.EXISTS', KEYS[1], ARGV[1])
if exist == 0 then
  return {3}
end

-- 4) 落到 DB
return {4}
