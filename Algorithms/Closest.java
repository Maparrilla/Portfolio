//Michael Parrilla
//Programming Question 2

public class Closest{
	
	private static int dif = 999999999;
	private static Node closest;
	
	public static int lookup(Node node, int k){
		int temp = Math.abs(k-node.info);
		if(temp<=dif){
			dif = temp;
			closest = node;
			if(k<node.info){
				lookup(node.left);
			}else{
				lookup(node.right);
			}
		}else{
			return closest.info;
		}
	}
}