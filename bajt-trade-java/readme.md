# Bajt Trade Project

## Overview
The Bajt Trade project aims to simulate a market where agents (Workers and Speculators) compete to acquire the most diamonds. The simulation operates in turns (days), with each turn consisting of several stages involving production, trading, and consumption activities.

## Agents

### Workers
- **Learning vs. Working**: Workers can either learn or work each day. Learning improves their career path, while working involves producing and trading goods.
- **Production**: Based on their productivity vector and bonuses, Workers produce goods such as food, clothing, tools, and software.
- **Consumption**: Workers consume food, tools, clothing, and software each day they work.
- **Strategies**:
  - **Hard Worker**: Always works, never learns.
  - **Saver**: Learns only when they have enough diamonds.
  - **Student**: Learns if they can afford food for the future.
  - **Periodic Learner**: Learns on a fixed schedule.
  - **Random Learner**: Randomly chooses to learn based on a probability.

### Speculators
- **Trading**: Speculators trade goods on the market, using their diamond budget to buy and sell goods.
- **Strategies**:
  - **Average Speculator**: Trades based on the average price of the last few days.
  - **Convex Speculator**: Buys when prices are increasing and sells when they are decreasing.
  - **Market Regulator**: Adjusts prices based on the number of goods available.

## Market
- **Matching Offers**: The market matches buy and sell offers between Workers and Speculators, determining transaction prices.
- **Unmatched Offers**: Unmatched Worker offers are bought by the market at the lowest price from the previous day.

## Goods
- **Diamonds**: The ultimate currency in the game.
- **Food**: Essential for Workers; 100 units consumed per working day.
- **Clothing**: Worn by Workers, with varying levels of durability.
- **Tools**: Provide productivity bonuses and are consumed daily.
- **Software**: Used in production, with quality affecting the output.

## Career Paths
- **Farmer**: Produces food.
- **Miner**: Produces diamonds.
- **Craftsman**: Produces clothing.
- **Engineer**: Produces tools.
- **Programmer**: Produces software.

Each career path provides productivity bonuses that increase with the level.

## Conclusion
The Bajt Trade project involves creating a market simulation with strategic decision-making for Workers and Speculators to maximize their acquisition of diamonds through learning, production, and trading activities.
