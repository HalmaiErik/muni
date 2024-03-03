import RefreshIcon from '@mui/icons-material/Refresh';
import { Card, CardContent, CardMedia, Chip, IconButton, LinearProgress, Stack, Tooltip, Typography } from "@mui/material";
import { DatePicker, LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs, { Dayjs } from 'dayjs';
import { useEffect, useState } from 'react';
import { useParams } from "react-router-dom";
import { useAccountFullInfo, useAccountStats, useCustomerCategories, useRefreshAccountInfo } from "../../api/bank-account-data-api";
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
    const { data: categories, isLoading: categoriesLoading, isRefetching: categoriesRefetching } = useCustomerCategories(currentUser);
    const { data: stats, isLoading: statsLoading, isRefetching: statsRefetching } = useAccountStats(currentUser, { from: fromDate.toDate(), to: toDate.toDate() }, accountExternalId);
    const { mutate: refreshAccountInfo, isLoading: isRefreshing } = useRefreshAccountInfo();

    useEffect(() => {
        if (accountInfo?.transactions.length === 0) {
            refreshInfo();
        }
    }, [accountInfo])

    const refreshInfo = () => {
        if (accountExternalId) {
            refreshAccountInfo(accountExternalId);
        }
    };

    if (accountInfoLoading || accountInfoRefetching
        || statsLoading || statsRefetching
        || categoriesLoading || categoriesRefetching
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
        <div style={{ maxWidth: 1256, margin: 'auto' }}>
            {accountInfo && (
                <>
                    <Card sx={{ marginBottom: '32px' }} variant="elevation">
                        <CardContent>
                            <div style={{ display: "flex", flexWrap: 'wrap' }}>
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
                                        <Typography variant="h2">{accountInfo.account.name !== null ? accountInfo.account.name : `${accountInfo.account.institutionName} account`}</Typography>
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

                    {categories && <CategoryList accountExternalId={accountExternalId} categories={categories} />}

                    {accountInfo.transactions.length === 0 && (
                        <Typography sx={{ textAlign: 'center' }} variant='h5' color="#616161">We could not find any transactions...</Typography>
                    )}

                    {accountInfo.transactions.length !== 0 && stats && (
                        <Card sx={{ marginBottom: '32px' }}>
                            <CardContent>
                                <LocalizationProvider dateAdapter={AdapterDayjs}>
                                    <div style={{ display: 'flex', gap: '8px', marginBottom: '16px' }}>
                                        <DatePicker
                                            sx={{ width: '192px' }}
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

                    {accountInfo.transactions.length !== 0 && categories && (
                        <Card variant="outlined">
                            <TransactionsTable transactions={accountInfo.transactions} categories={categories} />
                        </Card>
                    )}
                </>
            )
            }
        </div >
    );
};

export default Account;