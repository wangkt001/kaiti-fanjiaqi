import re
import sys

filename = sys.argv[1] if len(sys.argv) > 1 else 'LikeController.java'

# 读取文件
with open(filename, 'r', encoding='utf-8') as f:
    content = f.read()

# 删除 import 语句
content = re.sub(r'import com\.campus\.yujianhaowu\.interceptor\.AuthInterceptor;\s*', '', content)

# 替换 USER_ID_ATTR
content = re.sub(r'AuthInterceptor\.USER_ID_ATTR', '"userId"', content)

# 写回文件
with open(filename, 'w', encoding='utf-8', newline='') as f:
    f.write(content)

print(f'Fixed {filename}')
