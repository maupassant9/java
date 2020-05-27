%%
% File Name: LaneTemperatureVisualizer.m
% Author: Dong Xia
% Date: 2020/05/15
% Description: 
%  (1) Read the lane log and filter the temperature information
%       for simplicity: the temperature range should between 10 - 80 degree
%       any other value should be checked
%  (2) Draw the temperature
%% Clear the enviorment
clc;clear;

%% Constant definition
tempStringLength = 3;
timeStringLength = 19;

%% File Read
[file,path] = uigetfile('*.txt', 'Select a lane log file');

% if file is not selected by user
if isequal(file,0)
   return;     
end

% read all the string into mem
fileContent = fileread(strcat(path,file));

%% Filter temperature and date/time

% search string and get the temperature location
idxsDegreeTail = strfind(fileContent, "°C.");
idxsDegreeHead= idxsDegreeTail - tempStringLength;
stepTemp = ones(length(idxsDegreeHead),1)*[0:1:tempStringLength-1];
idxsTempIndxMatrix = (ones(tempStringLength,1)*idxsDegreeHead)';
idxsTempIndxMatrix = stepTemp + idxsTempIndxMatrix;

% search string and get the time stamp
idxsTimeTail = strfind(fileContent, " > Current temperature ");
idxsTimeHead = idxsTimeTail - timeStringLength;
stepTime = ones(length(idxsTimeHead),1)*[0:1:timeStringLength-1];
idxsTimeIndxMatrix = (ones(timeStringLength,1)*idxsTimeHead)';
idxsTimeIndxMatrix = stepTime + idxsTimeIndxMatrix;

% get time string and temperature string
tempStrings = fileContent(idxsTempIndxMatrix);
timeStrings = fileContent(idxsTimeIndxMatrix);

% clear temperary variables
clear fileContent;
clear idxsDegreeHead;
clear idxsDegreeTail;
clear idxsTimeTail;
clear idxsTimeHead;
clear idxsDegreeHead;
clear idxsTemp;
clear idxsTempIndxMatrix;
clear idxsTimeIndxMatrix;
clear stepTemp; clear stepTime;

%% convert to data struct and int struct
timeStamp = datetime(timeStrings,'InputFormat','yyyy-MM-dd HH:mm:ss');
tempInts = str2num(tempStrings);

clear timeStrings; clear tempStrings;

%% Draw temperature
figure(1);plot(timeStamp',tempInts', 'xr');
ylabel("Asphalt Temperature");
xlabel("Date & Time");
ylim([10,80]);
grid on;
