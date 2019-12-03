//Michael Parrilla
//Programming Question 1

public class MaxDepth{
	public static int maxDepth(Node node){
		if(node == null){
			return 0;
		}else{
			if(node.left == null && node.right == null){
				return 0;
			}else{
				int left = 1 + maxDepth(node.left);
				int right = 1 + maxDepth(node.right);
				if(left >= right){
					return left;
				}else{
					return right;
				}
			}			
		}
	}
}