import RefreshIcon from '@mui/icons-material/Refresh';
import { Card, CardContent, CardMedia, Chip, IconButton, LinearProgress, Stack, Tooltip, Typography } from "@mui/material";
import { DatePicker, LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs, { Dayjs } from 'dayjs';
import { useState } from 'react';
import { useParams } from "react-router-dom";
import { useAccountFullInfo, useGetAccountStats, useRefreshAccountInfo } from "../../api/bank-account-data-api";
import CategoryList from "../../components/category-list/CategoryList";
import ChartList from "../../components/chart-list/ChartList";
import TransactionsTable from "../../components/transactions-table/TransactionsTable";
import { useAuth } from "../../contexts/AuthContext";
import { formatToUsd } from "../../utils/currencyFormatUtils";

const Account = () => {
    const today = new Date();
    const firstOfThisMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    const [fromDate, setFromDate] = useState<Dayjs>(dayjs(firstOfThisMonth));
    const [toDate, setToDate] = useState<Dayjs>(dayjs(today));

    const { accountExternalId } = useParams();
    const { currentUser } = useAuth();
    const { data: accountInfo, isLoading: accountInfoLoading, isRefetching: accountInfoRefetching } = useAccountFullInfo(currentUser, accountExternalId);
    const { data: stats, isLoading: statsLoading, isRefetching: statsRefetching } = useGetAccountStats(currentUser, { from: fromDate.toDate(), to: toDate.toDate() }, accountExternalId);
    const { mutate: refreshAccountInfo, isLoading: isRefreshing } = useRefreshAccountInfo();

    const refreshInfo = () => {
        if (accountExternalId) {
            refreshAccountInfo(accountExternalId);
        }
    };

    if (accountInfoLoading || accountInfoRefetching
        || statsLoading || statsRefetching
        || isRefreshing) {
        return (
            <div style={{ maxWidth: 1256, margin: 'auto' }}>
                <LinearProgress />
            </div>
        );
    }

    const changeFromDate = (val: Dayjs | null) => {
        if (val) {
            setFromDate(val);
        }
    };

    const changeToDate = (val: Dayjs | null) => {
        if (val) {
            setToDate(val);
        }
    };

    return (
        <div style={{ maxWidth: 1256, margin: 'auto', padding: '32px' }}>
            {accountInfo && (
                <>
                    <Card sx={{ marginBottom: '32px' }} variant="elevation">
                        <CardContent>
                            <div style={{ display: "flex" }}>
                                <CardMedia
                                    sx={{
                                        height: 90,
                                        width: 90,
                                        alignSelf: 'center',
                                        flexDirection: 'column',
                                        marginRight: '24px'
                                    }}
                                    component="img"
                                    image={accountInfo.account.institutionLogo}
                                />

                                <Stack sx={{ flexGrow: 1 }} spacing={0.1}>
                                    <Stack direction="row" spacing={1.5}>
                                        <Typography variant="h2">{accountInfo.account.name}</Typography>
                                        <Chip sx={{ alignSelf: 'center' }} label={accountInfo.account.status} color={accountInfo.account.status === 'ACTIVE' ? 'success' : 'error'} size="small" />
                                        <Tooltip title="Refresh info">
                                            <IconButton size="large" onClick={refreshInfo}>
                                                <RefreshIcon fontSize="inherit" />
                                            </IconButton>
                                        </Tooltip>
                                    </Stack>

                                    <Typography variant="subtitle1">{accountInfo.account.iban}</Typography>
                                    <Typography variant="subtitle1">{accountInfo.account.institutionName}</Typography>
                                    <Typography variant="subtitle1">{`Account currency: ${accountInfo.account.currency}`}</Typography>
                                </Stack>

                                <Typography variant="h4" style={{ alignSelf: 'center' }}>{`Balance: ${formatToUsd(accountInfo.account.balance)}`}</Typography>
                            </div>
                        </CardContent>
                    </Card>

                    <CategoryList accountExternalId={accountExternalId} categories={accountInfo.categories} />

                    {stats && (
                        <Card sx={{ marginBottom: '32px' }}>
                            <CardContent>
                                <LocalizationProvider dateAdapter={AdapterDayjs}>
                                    <div style={{ display: 'flex', marginBottom: '32px' }}>
                                        <DatePicker
                                            sx={{ width: '192px', marginRight: '8px' }}
                                            label="From"
                                            slotProps={{ textField: { size: 'small' } }}
                                            onChange={changeFromDate}
                                            value={fromDate}
                                            disableFuture
                                        />
                                        <DatePicker
                                            sx={{ width: '192px', marginRight: '8px' }}
                                            label="To"
                                            slotProps={{ textField: { size: 'small' } }}
                                            onChange={changeToDate}
                                            value={toDate}
                                        />
                                    </div>
                                </LocalizationProvider>

                                <ChartList stats={stats} />
                            </CardContent>
                        </Card>
                    )}

                    <Card variant="outlined">
                        <TransactionsTable transactions={accountInfo.transactions} categories={accountInfo.categories} />
                    </Card>
                </>
            )}
        </div>
    );
};

export default Account;