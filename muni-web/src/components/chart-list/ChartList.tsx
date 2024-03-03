import { Typography } from "@mui/material";
import { BarChart } from "@mui/x-charts";
import { StatsDto } from "../../api/dtos";
import { formatToUsd } from "../../utils/currencyFormatUtils";

type Props = {
    stats: StatsDto;
};

const ChartList = ({ stats }: Props) => {
    return (
        <>
            <div style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'center', gap: '8px' }}>
                <Typography variant='h6' color='#66BB6A'>{`Income: ${formatToUsd(stats.inAmount)}`}</Typography>
                <Typography variant='h6'>/</Typography>
                <Typography variant='h6' color='#e15759'>{`Spent: ${formatToUsd(stats.outAmount)}`}</Typography>
            </div>
            <div style={{ display: 'flex' }}>
                <BarChart
                    height={312}
                    xAxis={[{ scaleType: 'band', data: ['Spending per category'] }]}
                    series={stats.categorySpentAmounts.map((categorySpentAmount) => (
                        {
                            data: [categorySpentAmount.spentAmount],
                            color: categorySpentAmount.categoryColorCode,
                            label: categorySpentAmount.categoryName
                        }
                    ))} />
            </div>
        </>

    );
};

export default ChartList;