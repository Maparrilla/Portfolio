% Part 1 Plot

fid1 = fopen('1pillaroutcmafit.dat');
d1 = textscan(fid1,'%s %u %s %s %f %s %s %s %s %s','headerlines',1);
fclose(fid1);
x1 = d1{2};
y1 = d1{5};

fid2 = fopen('2pillaroutcmafit.dat');
d2 = textscan(fid2,'%s %u %s %s %f %s %s %s %s %s %s %s','headerlines',1);
fclose(fid2);
x2 = d2{2};
y2 = d2{5};
d2{2}

fid3 = fopen('3pillaroutcmafit.dat');
d3 = textscan(fid3,'%s %u %s %s %f %s %s %s %s %s %s %s %s %s','headerlines',1);
fclose(fid3);
x3 = d3{2};
y3 = d3{5};

fid4 = fopen('4pillaroutcmafit.dat');
d4 = textscan(fid4,'%s %u %s %s %f %s %s %s %s %s %s %s %s %s %s %s','headerlines',1);
fclose(fid4);
x4 = d4{2};
y4 = d4{5};

p = plot(x1,y1,x2,y2,x3,y3,x4,y4,'LineWidth',2);
set(gca, 'FontName', 'Calibri');
title('Convergence of CMA');
xlabel('evaluation');
ylabel('objective function value');
legend('1 pillar', '2 pillar', '3 pillar', '4 pillar');

% Part 2 Plot
figure;

fidsf = fopen('sfoutcmafit.dat');
dsf = textscan(fidsf,'%s %u %s %s %f %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s','headerlines',1);
fclose(fidsf);
xsf = dsf{2};
ysf = dsf{5};
p = plot(xsf, ysf, 'LineWidth', 2);
set(gca, 'FontName', 'Calibri');
title('Convergence of CMA');
xlabel('evaluation');
ylabel('objective function value');

dsf{2}

