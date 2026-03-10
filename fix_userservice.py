import re

# 读取文件
with open('UserServiceImpl.java', 'r', encoding='utf-8') as f:
    content = f.read()

# 删除 JwtUtil 的 import
content = re.sub(r'import com\.campus\.yujianhaowu\.util\.JwtUtil;\s*', '', content)

# 删除 jwtUtil 字段
content = re.sub(r'\s*private final JwtUtil jwtUtil;\s*', '', content)

# 删除 login 方法中生成 token 的代码
content = re.sub(r'\s*String token = jwtUtil\.generateToken\(userId\);\s*', '', content)
content = re.sub(r'\s*String refreshToken = jwtUtil\.generateRefreshToken\(userId\);\s*', '', content)
content = re.sub(r'\s*result\.put\("token", token\);\s*', '', content)
content = re.sub(r'\s*result\.put\("refreshToken", refreshToken\);\s*', '', content)

# 写回文件
with open('UserServiceImpl.java', 'w', encoding='utf-8', newline='') as f:
    f.write(content)

print('Done!')
