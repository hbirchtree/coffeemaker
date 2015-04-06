--require "coffeeblocks.scripting.SceneManagerBinding"

--print(sceneman.clearcolor())

print("hello world!")

--Testing functions

function testfunc(num1,num2)
	return num1+num2
end

print(testfunc(1,2))

--Passing function as parameter

function doer(func,int1,int2)
	return func(int1,int2)
end

--Testing boolean expressions

local booltest = true
local number = 1.0

if (booltest)
then
	print("It works!")
end

local numbertest = 1

while(numbertest<=2)
do
	print("Still works!")
	numbertest=numbertest+1
	print(doer(testfunc,numbertest,numbertest-1))
end
