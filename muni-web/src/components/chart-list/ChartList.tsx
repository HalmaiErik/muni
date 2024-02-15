import { BarChart, PieChart } from "@mui/x-charts";
import { StatsDto } from "../../api/dtos";
import { Card, CardContent } from "@mui/material";

type Props = {
    stats: StatsDto;
};

const ChartList = ({ stats }: Props) => {
    return (
        <>
            <PieChart
                height={256}
                width={384}
                series={[{
                    data: [
                        { id: 0, value: stats.inAmount, label: 'Income', color: '#66BB6A' },
                        { id: 1, value: stats.outAmount, label: 'Spent', color: '#e15759' }
                    ]
                }]} />

            <BarChart
                height={256}
                width={448}
                xAxis={[{ scaleType: 'band', data: ['Spending per category'] }]}
                series={stats.categorySpentAmounts.map((categorySpentAmount) => (
                    {
                        data: [categorySpentAmount.spentAmount],
                        color: categorySpentAmount.categoryColorCode,
                        label: categorySpentAmount.categoryName
                    }
                ))} />
        </>
    );
};

export default ChartList;