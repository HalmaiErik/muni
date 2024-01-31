import { Button, Card, CardActionArea, CardContent, CardMedia, Chip, Grid, Paper, Stack, Typography } from "@mui/material";
import { AccountDto } from "../../api/dtos";
import styles from "./AccountsList.module.css"
import { useNavigate } from "react-router-dom";

type Props = {
    accounts: AccountDto[];
};

const AccountsList = ({ accounts }: Props) => {
    const navigate = useNavigate();

    const onSelectAccount = (accountExternalId: string) => {
        navigate(`/${accountExternalId}`);
    };

    return (
        <div>
            {accounts.map((account, index) =>
                <div key={index} className={styles.container}>
                    <Card>
                        <CardContent
                            sx={{
                                display: 'flex',
                            }}
                        >
                            <div style={{ flexGrow: 1 }}>
                                <Grid container>
                                    <CardMedia
                                        sx={{
                                            height: 60,
                                            width: 60,
                                            alignSelf: 'center',
                                            flexDirection: 'column',
                                            marginRight: '24px'
                                        }}
                                        component="img"
                                        image={account.institutionLogo}
                                    />
                                    <Stack spacing={0.2}>
                                        <Stack direction="row" spacing={1.5}>
                                            <h2>{account.name}</h2>
                                            <Chip sx={{ alignSelf: 'center' }} label={account.status} color={account.status === 'ACTIVE' ? 'success' : 'error'} size="small" />
                                        </Stack>
                                        <p>{account.iban}</p>
                                        <p>{account.institutionName}</p>
                                    </Stack>
                                </Grid>
                            </div>

                            <Button
                                sx={{
                                    maxHeight: '48px',
                                    alignSelf: 'center'
                                }}
                                disabled={false}
                                size="medium"
                                variant="outlined"
                                onClick={() => onSelectAccount(account.externalId)}
                            >
                                View details
                            </Button>
                        </CardContent>
                    </Card>
                </div>
            )
            }
        </div >
    );
};

export default AccountsList;