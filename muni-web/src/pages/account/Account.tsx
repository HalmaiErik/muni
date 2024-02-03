import { useEffect, useState } from "react";
import { AccountFullInfoDto, TransactionDto } from "../../api/dtos";
import { useParams } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import { getAccountFullInfo } from "../../api/bank-account-data-api";
import TransactionsTable from "../../components/transactions-table/TransactionsTable";
import { Card, CardContent, CardMedia, Chip, Paper, Stack, Typography } from "@mui/material";
import { formatToUsd } from "../../utils/currencyFormatUtils";

const Account = () => {
    const [accountInfo, setAccountInfo] = useState<AccountFullInfoDto>();
    const { accountExternalId } = useParams();
    const { currentUser } = useAuth();

    useEffect(() => {
        if (accountExternalId && currentUser) {
            getAccountFullInfo({ accountExternalId, refresh: false })
                .then((data) => {
                    console.log(data);
                    setAccountInfo(data);
                })
                .catch();
        }
    }, []);

    return (
        <>
            {accountInfo && (
                <>
                    <Card sx={{ maxWidth: 1256, margin: 'auto', marginBottom: '32px' }} variant="elevation">
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
                                    </Stack>
                                    <Typography variant="subtitle1">{accountInfo.account.iban}</Typography>
                                    <Typography variant="subtitle1">{accountInfo.account.institutionName}</Typography>
                                    <Typography variant="subtitle1">{`Currency: ${accountInfo.account.currency}`}</Typography>
                                </Stack>
                                <Typography variant="h4" style={{ alignSelf: 'center' }}>{`Balance: ${formatToUsd(accountInfo.account.balance)}`}</Typography>
                            </div>
                        </CardContent>
                    </Card>

                    <Card sx={{ maxWidth: 1256, maxHeight: 512, margin: 'auto', overflow: 'auto' }} variant="outlined">
                        <TransactionsTable transactions={accountInfo.transactions} />
                    </Card>
                </>
            )}
        </>
    );
};

export default Account;